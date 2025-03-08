import React from 'react';

interface AnnotatorFooterProps {
  frameCount: number;
  imagesPerPage: number;
  pageNum: number;
  setPageNum: React.Dispatch<React.SetStateAction<number>>;
}

export default function AnnotatorFooter({
  frameCount,
  imagesPerPage,
  pageNum,
  setPageNum
}: AnnotatorFooterProps) {
  const nextPage = () => {
    if ((pageNum + 1) * imagesPerPage < frameCount) {
      setPageNum(pageNum + 1);
    }
  };

  const prevPage = () => {
    if (pageNum > 0) {
      setPageNum(pageNum - 1);
    }
  };

  return (
    <div className="pagination-buttons">
      <button onClick={prevPage} disabled={pageNum === 0}>
        Back
      </button>
      <button onClick={nextPage} disabled={(pageNum + 1) * imagesPerPage >= frameCount}>
        Next
      </button>
    </div>
  );
}
