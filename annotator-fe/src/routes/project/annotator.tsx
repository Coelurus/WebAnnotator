import React, { useEffect, useState, useRef, useLayoutEffect } from 'react';
import { LoaderFunction, useLoaderData } from 'react-router-dom';
import { fetchProject } from '../../persistence/fetcher/fetcher';
import { ProjectResponse } from '../../persistence/model/responses';
import { Annotation, Label } from '../../persistence/model/responses';
import '../../styles/galery.css';
import { request } from '../../security/auth';
import AnnotatorHeader from './annotator-header';
import AnnotatorFooter from './annotator-footer';
import AnnotatorContent from './annotator-content';
export const loader: LoaderFunction = async ({ params }) => {
  const project = await fetchProject(Number(params.projectId));
  return project;
};

export default function Project() {
  const UNDEFINED = -1;
  const LEFT_BUTTON = 0;
  const RIGHT_BUTTON = 2;
  const DEFAULT_IMAGE_SIZE = 50;

  const [pageNum, setPageNum] = useState<number>(0);
  const [frameCount, setFrameCount] = useState<number>(0);
  const [startIndex, setStartIndex] = useState<number>(UNDEFINED);
  const [endIndex, setEndIndex] = useState<number>(UNDEFINED);
  const [selectedFrames, setSelectedFrames] = useState<Annotation[]>([]);
  const project = useLoaderData() as ProjectResponse;
  const [imagesPerPage, setImagesPerPage] = useState<number>(100);
  const [labels, setLabels] = useState<Label[]>([]);
  const [currentLabel, setCurrentLabel] = useState<Label>();
  const [pressedButton, setPressedButton] = useState<number>(UNDEFINED);
  const [imageSize, setImageSize] = useState<number>(DEFAULT_IMAGE_SIZE);

  const gridRef = useRef<HTMLDivElement>(null);

  useLayoutEffect(() => {
    const updateImagesPerPage = () => {
      if (gridRef.current) {
        const gridWidth = gridRef.current.clientWidth;
        const gridHeight = gridRef.current.clientHeight;
        const columns = Math.floor(gridWidth / imageSize);
        const rows = Math.floor(gridHeight / imageSize);
        setImagesPerPage(columns * rows);
        console.log(gridWidth, gridHeight);
      }
    };

    updateImagesPerPage();
    window.addEventListener('resize', updateImagesPerPage);
    return () => window.removeEventListener('resize', updateImagesPerPage);
  }, [imageSize]);

  useEffect(() => {
    request('GET', `/api/projects/${project.id}/frame/count`)
      .then((response) => setFrameCount(response.data.count))
      .catch((error) => console.error('Error fetching frame count:', error));

    request('GET', `/api/projects/${project.id}/annotations`)
      .then((response) => setSelectedFrames(response.data))
      .catch((error) => console.error('Error fetching annotations:', error));
  }, [project.id]);

  useEffect(() => {
    request(`GET`, `/api/labels`)
      .then((response) => {
        setLabels(response.data);
        setCurrentLabel(response.data[0]);
      })
      .catch((error) => console.error('Error fetching labels:', error));
  }, []);

  const handleMouseDown = (event: React.MouseEvent, frameId: number) => {
    setPressedButton(event.nativeEvent.button);
    setStartIndex(frameId);
  };

  const handleMouseOver = (frameId: number) => {
    setEndIndex(frameId);
  };

  const handleMouseUp = () => {
    if (startIndex === UNDEFINED) {
      return;
    }

    if (!currentLabel) {
      //TODO - add better alerting...
      alert('Label not chosen...');
      return;
    }

    let lowerIndex: number;
    let higherIndex: number;
    if (startIndex > endIndex) {
      lowerIndex = endIndex;
      higherIndex = startIndex;
    } else {
      lowerIndex = startIndex;
      higherIndex = endIndex;
    }

    if (pressedButton === RIGHT_BUTTON) {
      request('POST', `/api/projects/${project.id}/erase/${lowerIndex}/${higherIndex}`);

      const withoutErased = selectedFrames.filter(
        (annotation) => annotation.frameId < lowerIndex || annotation.frameId > higherIndex
      );
      setSelectedFrames(withoutErased);
    }

    if (currentLabel && pressedButton === LEFT_BUTTON) {
      request(
        'POST',
        `/api/projects/${project.id}/annotate/${lowerIndex}/${higherIndex}/label/${currentLabel?.id}`
      );

      const framesToAdd: Annotation[] = [];
      for (let index = lowerIndex; index <= higherIndex; index++) {
        if (lowerIndex != UNDEFINED && !selectedFrames.some((frame) => frame.frameId === index)) {
          framesToAdd.push({ frameId: index, labelId: currentLabel.id });
        }
      }
      setSelectedFrames((selectedFrames) => [...selectedFrames, ...framesToAdd]);
    }

    setStartIndex(UNDEFINED);
    setEndIndex(UNDEFINED);
  };

  return (
    <div
      onMouseUp={() => handleMouseUp()}
      onContextMenu={(event) => {
        event.preventDefault();
      }}
    >
      <AnnotatorHeader
        imageSize={imageSize}
        setImageSize={setImageSize}
        currentLabel={currentLabel}
        setCurrentLabel={setCurrentLabel}
        labels={labels}
        setLabels={setLabels}
        project={project}
      />

      <AnnotatorContent
        gridRef={gridRef}
        pageNum={pageNum}
        imagesPerPage={imagesPerPage}
        frameCount={frameCount}
        imageSize={imageSize}
        labels={labels}
        selectedFrames={selectedFrames}
        project={project}
        handleMouseDown={handleMouseDown}
        handleMouseOver={handleMouseOver}
      />

      <AnnotatorFooter
        frameCount={frameCount}
        imagesPerPage={imagesPerPage}
        pageNum={pageNum}
        setPageNum={setPageNum}
      />
    </div>
  );
}
