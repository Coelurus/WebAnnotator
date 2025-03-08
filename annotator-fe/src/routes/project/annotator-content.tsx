import React from 'react';
import { Annotation, Label, ProjectResponse } from '../../persistence/model/responses';
import { request } from '../../security/auth';

interface AnnotatorContentProps {
  gridRef: React.RefObject<HTMLDivElement>;
  pageNum: number;
  imagesPerPage: number;
  frameCount: number;
  imageSize: number;
  labels: Label[];
  selectedFrames: Annotation[];
  project: ProjectResponse;
  handleMouseDown: (event: React.MouseEvent, frameId: number) => void;
  handleMouseOver: (frameId: number) => void;
}

export default function AnnotatorContent({
  gridRef,
  pageNum,
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

  const startPosition = pageNum * imagesPerPage + 1;
  const endPosition = Math.min(startPosition + imagesPerPage - 1, frameCount);
  const imagePositions = Array.from(
    { length: endPosition - startPosition + 1 },
    (_, i) => startPosition + i
  );

  const selectedImageStyle = (index: number) => {
    const frame = selectedFrames.find((frame) => frame.frameId === index);
    return {
      width: `${imageSize}px`,
      height: `${imageSize}px`,
      borderColor: frame ? labels[frame.labelId].color : '',
      borderWidth: '5px',
      borderStyle: 'solid'
    };
  };

  const [imageUrls, setImageUrls] = React.useState<{ [key: number]: string }>({});

  React.useEffect(() => {
    const loadImages = async () => {
      const newImageUrls: { [key: number]: string } = {};
  
      await Promise.all(
        imagePositions.map(async (position) => {
          try {
            const response = await request('GET', `/api/projects/${project.id}/frame/${position - 1}`, {}, ''); // No content type needed
            const blob = new Blob([response.data]);
            const imageUrl = URL.createObjectURL(blob);
            newImageUrls[position] = imageUrl;
          } catch (error) {
            console.error(`Error fetching image for position ${position}:`, error);
          }
        })
      );
  
      setImageUrls(newImageUrls);
    };
  
    loadImages();
    
  }, [project.id]); //[project.id, imagePositions]

  return (
    <div className="image-grid" ref={gridRef}>
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
            src={imageUrls[position] || ''} // Use fetched image URL
            alt={`Frame ${position}`}
            className="image"
            style={selectedImageStyle(position)}
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
