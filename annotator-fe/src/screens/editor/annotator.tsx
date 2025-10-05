import React from 'react';
import { LoaderFunction, useLoaderData } from 'react-router-dom';
import {
  fetchAnnotations,
  fetchFrameCount,
  fetchLabels,
  fetchProject
} from '../../persistence/requests/fetcher';
import { Annotation, Label, Project as ProjectType } from '../../persistence/model/data';
import AnnotatorHeader from './annotator-header';
import AnnotatorFooter from './annotator-footer';
import AnnotatorContent from './annotator-content';
import { postAddAnnotations, postEraseAnnotations } from '../../persistence/requests/poster';
import toast, { Toaster } from 'react-hot-toast';

/**
 * Loader function to fetch the project data based on the project ID from the URL parameters.
 * 
 * @param params Parameters from the URL, including the project ID.
 * @returns The project data fetched from the server.
 */
export const loader: LoaderFunction<ProjectType> = async ({ params }) => {
  const project = await fetchProject(Number(params.projectId));
  return project;
};

/**
 * Project component serves as the main entry point for the annotator screen.
 * 
 * @returns The main component for the annotator screen, which includes header, content, and footer.
 */
export default function Project() {
  /**
   * Constants for left mouse button identifier.
   */
  const LEFT_BUTTON = 0;
  /**
   * Constants for right mouse button identifier.
   */
  const RIGHT_BUTTON = 2;
  /**
   * Default size for images in the annotator grid.
   */
  const DEFAULT_IMAGE_SIZE = 50;
  /**
   * Gap size between images in the annotator grid.
   */
  const GAP_SIZE = 0;
  /**
   * Default number of images displayed per page in the annotator grid.
   */
  const DEFAULT_IMAGE_PER_PAGE = 100;
  /**
   * Default height for the annotator screen when specific height cannot be found.
   */
  const DEFAULT_SCREEN_HEIGHT = 500;

  // State to manage the current page number
  const [pageNum, setPageNum] = React.useState<number>(0);
  // State to manage the total number of frames in the project
  const [frameCount, setFrameCount] = React.useState<number>(0);
  // State to manage where user starts selecting frames
  const [startIndex, setStartIndex] = React.useState<number | null>(null);
  // State to manage where user ends selecting frames
  const [endIndex, setEndIndex] = React.useState<number | null>(null);
  // State to manage the selected frames for annotation
  const [selectedFrames, setSelectedFrames] = React.useState<Annotation[]>([]);
  // State to manage the number of images displayed per page
  const [imagesPerPage, setImagesPerPage] = React.useState<number>(DEFAULT_IMAGE_PER_PAGE);
  // State to manage the list of labels available for annotation
  const [labels, setLabels] = React.useState<Label[]>([]);
  // State to manage the currently selected label for annotating
  const [currentLabel, setCurrentLabel] = React.useState<Label>();
  // State to manage the button pressed during mouse events
  const [pressedButton, setPressedButton] = React.useState<number | null>(null);
  // State to manage the size of the images displayed in the annotator grid
  const [imageSize, setImageSize] = React.useState<number>(DEFAULT_IMAGE_SIZE);

  /**
   * Loader function to fetch the project data when the component is mounted.
   */
  const project = useLoaderData() as ProjectType;

  //Refs to access DOM elements for layout calculations.
  /**
   * Reference to the main screen container.
   */
  const screenRef = React.useRef<HTMLDivElement>(null);
  /**
   * Reference to the grid container where images are displayed.
   */
  const gridRef = React.useRef<HTMLDivElement>(null);
  /**
   * Reference to the header element for accessing its size.
   */
  const headerRef = React.useRef<HTMLDivElement>(null);
  /**
   * Reference to the footer element for accessing its size.
   */
  const footerRef = React.useRef<HTMLDivElement>(null);

  // Effect to update the number of images per page based on the grid size and image size.
  React.useLayoutEffect(() => {
    /**
     * Updates the number of images displayed per page based on the current grid size and image size.
     */
    const updateImagesPerPage = () => {
      if (gridRef.current) {
        const gridWidth = gridRef.current.clientWidth;
        const gridHeight =
          (screenRef.current?.clientHeight ?? DEFAULT_SCREEN_HEIGHT) -
          ((headerRef.current?.clientHeight ?? 0) + (footerRef.current?.clientHeight ?? 0));
        const columns = Math.floor((gridWidth + GAP_SIZE) / (imageSize + GAP_SIZE));
        const rows = Math.floor((gridHeight + GAP_SIZE) / (imageSize + GAP_SIZE));       

        setImagesPerPage(columns * rows);
      }
    };

    updateImagesPerPage();
    window.addEventListener('resize', updateImagesPerPage);
    const observer = new MutationObserver(updateImagesPerPage);
    if (headerRef.current) {
      observer.observe(headerRef.current, { attributes: true, childList: true, subtree: true });
    }
    return () => window.removeEventListener('resize', updateImagesPerPage);
  }, [imageSize]);

  // Effect to fetch the frame count and annotations when the project ID changes.
  React.useEffect(() => {
    fetchFrameCount(project.id).then(setFrameCount);

    fetchAnnotations(project.id).then(setSelectedFrames);
  }, [project?.id]);

  // Effect to fetch the labels when the component is mounted.
  React.useEffect(() => {
    fetchLabels().then((labels) => {
      if (labels.length > 0) {
        setLabels(labels);
        setCurrentLabel(labels[0]);
      }
    });
  }, []);

  /**
   * Handles the mouse down event on a frame in the annotator grid.
   * 
   * @param event Mouse event triggered when the user presses the mouse button down on a frame.
   * @param frameId ID of the frame where the mouse button was pressed.
   */
  const handleMouseDown = (event: React.MouseEvent, frameId: number) => {
    setPressedButton(event.nativeEvent.button);
    setStartIndex(frameId);
  };

  /**
   * Handles the mouse over event on a frame in the annotator grid.
   * 
   * @param frameId ID of the frame where the mouse is currently hovering.
   */
  const handleMouseOver = (frameId: number) => {
    setEndIndex(frameId);
  };

  /**
   * Handles the mouse up event to finalize the selection of frames for annotation.
   */
  const handleMouseUp = () => {
    if (startIndex === null || endIndex === null) {
      return;
    }

    if (!currentLabel) {
      toast.error('Label not chosen');
      return;
    }

    // Determine the lower and higher index based on the start and end indices.
    let lowerIndex: number;
    let higherIndex: number;
    if (startIndex > endIndex) {
      lowerIndex = endIndex;
      higherIndex = startIndex;
    } else {
      lowerIndex = startIndex;
      higherIndex = endIndex;
    }

    // If the user pressed the right mouse button, erase annotations in the selected range.
    if (pressedButton === RIGHT_BUTTON) {
      postEraseAnnotations(project.id, lowerIndex, higherIndex);
      const withoutErased = selectedFrames.filter(
        (annotation) => annotation.frameId < lowerIndex || annotation.frameId > higherIndex
      );
      setSelectedFrames(withoutErased);
    }

    // If the user pressed the left mouse button, add annotations in the selected range.
    if (currentLabel && pressedButton === LEFT_BUTTON) {
      postAddAnnotations(project.id, lowerIndex, higherIndex, currentLabel?.id ?? 0);

      const framesToAdd: Annotation[] = [];
      for (let index = lowerIndex; index <= higherIndex; index++) {
        if (lowerIndex !== null && !selectedFrames.some((frame) => frame.frameId === index)) {
          framesToAdd.push({ frameId: index, labelId: currentLabel.id });
        }
      }
      setSelectedFrames((selectedFrames) => [...selectedFrames, ...framesToAdd]);
    }

    setStartIndex(null);
    setEndIndex(null);
  };

  return (
    <div
      ref={screenRef}
      style={{
        display: 'flex',
        flexDirection: 'column',
        height: '100vh',
        overflow: 'hidden'
      }}
      onMouseUp={handleMouseUp}
      onContextMenu={(event) => {
        event.preventDefault();
      }}
    >
      <Toaster position="top-right" reverseOrder={false} />

      <div style={{ flex: '0 0 auto' }}>
        <AnnotatorHeader
          headerRef={headerRef}
          imageSize={imageSize}
          setImageSize={setImageSize}
          currentLabel={currentLabel}
          setCurrentLabel={setCurrentLabel}
          labels={labels}
          setLabels={setLabels}
          project={project}
        />
      </div>

      <div style={{ flex: '1 1 auto', overflow: 'hidden' }}>
        <AnnotatorContent
          gridRef={gridRef}
          pageNum={pageNum}
          setPageNum={setPageNum}
          imagesPerPage={imagesPerPage}
          frameCount={frameCount}
          imageSize={imageSize}
          labels={labels}
          selectedFrames={selectedFrames}
          project={project}
          handleMouseDown={handleMouseDown}
          handleMouseOver={handleMouseOver}
        />
      </div>

      <div style={{ flex: '0 0 auto' }}>
        <AnnotatorFooter
          footerRef={footerRef}
          frameCount={frameCount}
          imagesPerPage={imagesPerPage}
          pageNum={pageNum}
          setPageNum={setPageNum}
        />
      </div>
    </div>
  );
}
