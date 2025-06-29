import React from 'react';

/**
 * Interface for the properties of the AnnotatorFooter component.
 */
export interface AnnotatorFooterProps {
  /**
   * Total number of frames in the current project.
   */
  frameCount: number;
  /**
   * Number of images to display per page.
   */
  imagesPerPage: number;
  /**
   * Current page number for pagination.
   */
  pageNum: number;
  /**
   * Function to set the current page number.
   */
  setPageNum: React.Dispatch<React.SetStateAction<number>>;
  /**
   * Reference to the footer element for for accessing its size.
   */
  footerRef: React.RefObject<HTMLDivElement>;
}

/**
 * AnnotatorFooter component provides pagination controls for navigating through image frames.
 * 
 * @param props The properties for the AnnotatorFooter component.
 * @returns The rendered footer with pagination buttons.
 */
export default function AnnotatorFooter({
  frameCount,
  imagesPerPage,
  pageNum,
  setPageNum,
  footerRef
}: AnnotatorFooterProps) {
  /**
   * Function to go to the next page of images.
   */
  const nextPage = () => {
    if ((pageNum + 1) * imagesPerPage < frameCount) {
      setPageNum(pageNum + 1);
    }
  };

  /**
   * Function to go to the previous page of images.
   */
  const prevPage = () => {
    if (pageNum > 0) {
      setPageNum(pageNum - 1);
    }
  };

  return (
    <div className="pagination-buttons" ref={footerRef}>
      <button onClick={prevPage} disabled={pageNum === 0}>
        Back
      </button>
      <button onClick={nextPage} disabled={(pageNum + 1) * imagesPerPage >= frameCount}>
        Next
      </button>
    </div>
  );
}
