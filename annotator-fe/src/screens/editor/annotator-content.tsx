import React from 'react';
import { Annotation, Label, Project } from '../../persistence/model/data';
import { getImageUrlRequest } from '../../persistence/requests/fetcher';
import { BUTTONS } from '../../config/path';

/**
 * Interface for the properties of the AnnotatorContent component.
 */
export interface AnnotatorContentProps {
  /**
   * Reference to the grid container for image frames.
   */
  gridRef: React.RefObject<HTMLDivElement>;
  /**
   * Current page number for pagination.
   */
  pageNum: number;
  /**
   * Function to set the current page number.
   */
  setPageNum: React.Dispatch<React.SetStateAction<number>>;
  /**
   * Number of images to display per page.
   */
  imagesPerPage: number;
  /**
   * Total number of frames in current project.
   */
  frameCount: number;
  /**
   * Size of each image in the grid.
   */
  imageSize: number;
  /**
   * List of labels to use for annotating.
   */
  labels: Label[];
  /**
   * List of currently selected frames by the user.
   */
  selectedFrames: Annotation[];
  /**
   * Current project being annotated.
   */
  project: Project;
  /**
   * Function to handle mouse down events on image frames.
   * 
   * @param event The mouse down event.
   * @param frameId The ID of the frame being interacted with.
   */
  handleMouseDown: (event: React.MouseEvent, frameId: number) => void;
  /**
   * Function to handle mouse over events on image frames.
   * 
   * @param frameId The ID of the frame being hovered over.
   */
  handleMouseOver: (frameId: number) => void;
  /**
   * Start index of the current selection.
   */
  startIndex: number | null;
  /**
   * End index of the current selection.
   */
  endIndex: number | null;
  /**
   * Currently pressed mouse button (if any).
   */
  pressedButton: number | null;
  /**
   * Color of the currently selected label.
   */
  currentLabelColor?: string;
}

/**
 * AnnotatorContent component renders a grid of images for annotation.
 * It handles image loading, selection, and interaction.
 * 
 * @param props The properties for the component.
 * @returns JSX.Element The rendered component.
 */
export default function AnnotatorContent({
  gridRef,
  pageNum,
  setPageNum,
  imagesPerPage,
  frameCount,
  imageSize,
  labels,
  selectedFrames,
  project,
  handleMouseDown,
  handleMouseOver,
  startIndex,
  endIndex,
  pressedButton,
  currentLabelColor,
}: AnnotatorContentProps) {

  // State to manage array of image currently displayed in the grid
  const [imagePositions, setImagePositions] = React.useState<number[]>([]);
  // State to manage sources of images, mapping frameId to image URL
  const [imageSources, setImageSources] = React.useState<Map<number, string>>(new Map());
  // Cache to store images to avoid re-fetching
  const [imageCache] = React.useState<Map<number, string>>(new Map());
  
  // Whenever page user is on changes or number of images per page changes, update the images shown in the grid
  React.useEffect(() => {
    const startPosition = pageNum * imagesPerPage + 1;
    const endPosition = Math.min(startPosition + imagesPerPage - 1, frameCount);
    
    setImagePositions(
      Array.from({ length: endPosition - startPosition + 1 }, (_, i) => startPosition + i)
    );
  }, [frameCount, imagesPerPage, pageNum]);

  // Whenever size of the images change, adjust the page number if necessary
  React.useEffect(() => {
    if ((pageNum + 1) * imagesPerPage >= frameCount && pageNum > 0) {
      setPageNum(pageNum - 1);
    }
  }, [imageSize]);

  React.useEffect(() => {
    const loadImages = async () => {
      const newImageSources = new Map();

      // Get sources for images that are currently displayed in the grid
      // If the image is already in the cache, use it, otherwise fetch it from the server
      // and store it in the cache for future use
      await Promise.all(       
        imagePositions.map(async (position) => {
          if (imageCache.has(position)) {
            newImageSources.set(position, imageCache.get(position)!);
          } else {
            const imageUrl = await getImageUrlRequest(project, position);
  
            imageCache.set(position, imageUrl);
            newImageSources.set(position, imageUrl);
          }
        })
      );     

      setImageSources(newImageSources);

      // Preload next images to improve performance when user navigates to the next page
      await Promise.all(
        imagePositions.map(async (position) => {
          const toCache = position + imagesPerPage
          if (toCache >= frameCount) return
          if (!imageCache.has(toCache)) {
            const imageUrl = await getImageUrlRequest(project, toCache);
            imageCache.set(toCache, imageUrl);
          }
        }
      )
      )
    };

    loadImages();
  }, [imagePositions]);
  
  /**
   * Function to determine the style of the selected image frame.
   * 
   * @param index The index of the image frame.
   * @returns The style object for the image frame.
   */
  const imageStyle = (position: number) => {
    const permanentFrame = selectedFrames.find((frame) => frame.frameId === position);
    const isInCurrentSelection = startIndex !== null && endIndex !== null &&
      position >= Math.min(startIndex, endIndex) && position <= Math.max(startIndex, endIndex);
    const currentFrameColor = labels.find((label) => label.id === permanentFrame?.labelId)?.color || 'white';

    let borderColor = 'white';
    let borderStyle = 'solid';

    if (permanentFrame && isInCurrentSelection && pressedButton === BUTTONS.RIGHT_BUTTON) {
      borderColor = currentFrameColor;
      borderStyle = 'dashed';
    }
    else if (permanentFrame) {
      borderColor = currentFrameColor;
    } else if (isInCurrentSelection && pressedButton === BUTTONS.LEFT_BUTTON) {
      borderColor = currentLabelColor || 'gray';
      borderStyle = 'dashed';
    }

    return {
      width: `${imageSize}px`,
      height: `${imageSize}px`,
      borderColor,
      borderWidth: '5px',
      borderStyle
    };
  };

  /**
   * Prevent drag handler to stop images from being dragged.
   * 
   * @param e The event triggered when an image is dragged.
   */
  const preventDragHandler = (e: Event) => {
    e.preventDefault();
  };

  return (
    <div className="d-flex flex-wrap image-grid" ref={gridRef}>
      {imagePositions.map((position) => (
        <div 
        id={`image-frame-wrapper-${position}`} 
        key={`image-frame-wrapper-${position}`}
        style={{
          position: 'relative',
        }}>
          <div
            className="image-frame-indicator"
            id={`image-frame-indicator-${position}`}
            key={`image-frame-indicator-${position}`}
            style={{
              position: 'absolute',
              top: '50%',
              left: '50%',
              transform: 'translate(-50%, -50%)',
              fontSize: '2rem',
              pointerEvents: 'none',
            }}
          ></div>
          <img
            id={`image-frame-${position}`}
            key={position}
            className="img-fluid"
            style={{
              objectFit: 'cover',
              cursor: 'pointer',
              border: '5px solid rgba(0, 0, 0, 0)',
              ...imageStyle(position)
            }}
            src={imageSources.get(position)}
            onMouseDown={(event) => handleMouseDown(event, position)}
            onMouseOver={() => handleMouseOver(position)}
            onDragStart={() => preventDragHandler}
            draggable="false"
          />
        </div>
      ))}
    </div>
  );
}
