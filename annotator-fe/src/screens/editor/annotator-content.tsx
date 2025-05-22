import React from 'react';
import { Annotation, Label, Project } from '../../persistence/model/data';
import { blobRequest } from '../../security/auth';

interface AnnotatorContentProps {
  gridRef: React.RefObject<HTMLDivElement>;
  pageNum: number;
  setPageNum: React.Dispatch<React.SetStateAction<number>>;
  imagesPerPage: number;
  frameCount: number;
  imageSize: number;
  labels: Label[];
  selectedFrames: Annotation[];
  project: Project;
  handleMouseDown: (event: React.MouseEvent, frameId: number) => void;
  handleMouseOver: (frameId: number) => void;
}

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
  handleMouseOver
}: AnnotatorContentProps) {
  const preventDragHandler = (e: Event) => {
    e.preventDefault();
  };

  const [imagePositions, setImagePositions] = React.useState<number[]>([]);
  const [imageSources, setImageSources] = React.useState<Map<number, string>>(new Map());
  const [imageCache] = React.useState<Map<number, string>>(new Map());

  React.useEffect(() => {
    const startPosition = pageNum * imagesPerPage + 1;
    const endPosition = Math.min(startPosition + imagesPerPage - 1, frameCount);

    setImagePositions(
      Array.from({ length: endPosition - startPosition + 1 }, (_, i) => startPosition + i)
    );
  }, [frameCount, imagesPerPage, pageNum]);

  const selectedImageStyle = (index: number) => {
    const frame = selectedFrames.find((frame) => frame.frameId === index);

    return {
      width: `${imageSize}px`,
      height: `${imageSize}px`,
      borderColor: frame ? labels[frame.labelId]?.color : '',
      borderWidth: '5px',
      borderStyle: 'solid'
    };
  };

  React.useEffect(() => {
    if ((pageNum + 1) * imagesPerPage >= frameCount && pageNum > 0) {
      setPageNum(pageNum - 1);
    }
  }, [imageSize]);

  React.useEffect(() => {
    const loadImages = async () => {
      const newImageSources = new Map();

      await Promise.all(       
        imagePositions.map(async (position) => {
          if (imageCache.has(position)) {
            newImageSources.set(position, imageCache.get(position)!);
          } else {
            const response = await blobRequest(`/api/projects/${project.id}/frame/${position - 1}`);
            const blob = new Blob([response.data], { type: response.headers['content-type'] });
            const imageUrl = URL.createObjectURL(blob);
  
            imageCache.set(position, imageUrl);
            newImageSources.set(position, imageUrl);
          }
        })
      );     

      setImageSources(newImageSources);

      await Promise.all(
        imagePositions.map(async (position) => {
          const toCache = position + imagesPerPage
          if (toCache >= frameCount) return
          if (!imageCache.has(toCache)) {
            console.log(`Caching image for position: ${toCache}`);
            
            const response = await blobRequest(`/api/projects/${project.id}/frame/${toCache - 1}`);
            const blob = new Blob([response.data], { type: response.headers['content-type'] });
            const imageUrl = URL.createObjectURL(blob);
            
            imageCache.set(toCache, imageUrl);
          }
        }
      )
      )
    };

    loadImages();
  }, [imagePositions]);

  return (
    <div className="d-flex flex-wrap gap-1 image-grid" ref={gridRef}>
      {imagePositions.map((position) => (
        <div id={`image-frame-wrapper-${position}`} key={`image-frame-wrapper-${position}`}>
          <div
            className="image-frame-indicator"
            id={`image-frame-indicator-${position}`}
            key={`image-frame-indicator-${position}`}
          ></div>
          <img
            id={`image-frame-${position}`}
            key={position}
            className="img-fluid"
            style={{
              objectFit: 'cover',
              cursor: 'pointer',
              border: '5px solid rgba(0, 0, 0, 0)',
              ...selectedImageStyle(position)
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
