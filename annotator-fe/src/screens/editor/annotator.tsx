import React, { useEffect, useState, useRef, useLayoutEffect } from 'react';
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

export const loader: LoaderFunction<ProjectType> = async ({ params }) => {
  const project = await fetchProject(Number(params.projectId));
  return project;
};

export default function Project() {
  const LEFT_BUTTON = 0;
  const RIGHT_BUTTON = 2;
  const DEFAULT_IMAGE_SIZE = 50;
  const GAP_SIZE = 5;

  const [pageNum, setPageNum] = useState<number>(0);
  const [frameCount, setFrameCount] = useState<number>(0);
  const [startIndex, setStartIndex] = useState<number | null>(null);
  const [endIndex, setEndIndex] = useState<number | null>(null);
  const [selectedFrames, setSelectedFrames] = useState<Annotation[]>([]);
  const [imagesPerPage, setImagesPerPage] = useState<number>(100);
  const [labels, setLabels] = useState<Label[]>([]);
  const [currentLabel, setCurrentLabel] = useState<Label>();
  const [pressedButton, setPressedButton] = useState<number | null>(null);
  const [imageSize, setImageSize] = useState<number>(DEFAULT_IMAGE_SIZE);

  const project = useLoaderData() as ProjectType;

  const screenRef = useRef<HTMLDivElement>(null);
  const gridRef = useRef<HTMLDivElement>(null);
  const headerRef = useRef<HTMLDivElement>(null);
  const footerRef = useRef<HTMLDivElement>(null);

  useLayoutEffect(() => {
    const updateImagesPerPage = () => {
      if (gridRef.current) {
        const gridWidth = gridRef.current.clientWidth;
        const gridHeight =
          (screenRef.current?.clientHeight ?? 500) -
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

  useEffect(() => {
    fetchFrameCount(project.id).then(setFrameCount);

    fetchAnnotations(project.id).then(setSelectedFrames);
  }, [project?.id]);

  useEffect(() => {
    fetchLabels().then((labels) => {
      if (labels.length > 0) {
        setLabels(labels);
        setCurrentLabel(labels[0]);
      }
    });
  }, []);

  const handleMouseDown = (event: React.MouseEvent, frameId: number) => {
    setPressedButton(event.nativeEvent.button);
    setStartIndex(frameId);
  };

  const handleMouseOver = (frameId: number) => {
    setEndIndex(frameId);
  };

  const handleMouseUp = () => {
    if (startIndex === null || endIndex === null) {
      return;
    }

    if (!currentLabel) {
      toast.error('Label not chosen');
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
      postEraseAnnotations(project.id, lowerIndex, higherIndex);
      const withoutErased = selectedFrames.filter(
        (annotation) => annotation.frameId < lowerIndex || annotation.frameId > higherIndex
      );
      setSelectedFrames(withoutErased);
    }

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
      onMouseUp={() => handleMouseUp()}
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
