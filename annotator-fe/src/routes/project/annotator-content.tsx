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
  const [imageSources, setImageSources] = React.useState<{ [key: number]: string }>({});

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
      console.log('(pageNum + 1) * imagesPerPage', (pageNum + 1) * imagesPerPage);
      console.log('frameCount', frameCount);

      setPageNum(pageNum - 1);
    }
  }, [imageSize]);

  React.useEffect(() => {
    const loadImages = async () => {
      const newImageSources: { [key: number]: string } = {};

      await Promise.all(
        imagePositions.map(async (position) => {
          const response = await blobRequest(`/api/projects/${project.id}/frame/${position - 1}`);

          const blob = new Blob([response.data], { type: response.headers['content-type'] });
          const imageUrl = URL.createObjectURL(blob);

          newImageSources[position] = imageUrl;
        })
      );

      setImageSources(newImageSources);
    };

    loadImages();
  }, [imagePositions]);

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
            alt={`Frame ${position}`}
            className="image"
            style={selectedImageStyle(position)}
            src={imageSources[position]}
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
