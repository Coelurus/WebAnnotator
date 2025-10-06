import React from 'react';
import Button from 'react-bootstrap/Button';

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

  /**
   * Configuration object for keyboard shortcuts
   */
  const keyboardShortcuts = React.useMemo(() => ({
    a: prevPage,
    d: nextPage,
  }), [pageNum, frameCount, imagesPerPage]);

  /**
   * Effect to handle keyboard shortcuts for navigation
   */
  React.useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      // Only trigger if not typing in an input field
      if (event.target instanceof Element &&
          !['INPUT', 'TEXTAREA', 'SELECT'].includes(event.target.tagName)) {
        
        const key = event.key.toLowerCase();
        const shortcutAction = keyboardShortcuts[key as keyof typeof keyboardShortcuts];
        
        if (shortcutAction) {
          event.preventDefault();
          shortcutAction();
        }
      }
    };

    // Add event listener
    document.addEventListener('keydown', handleKeyDown);

    // Cleanup function to remove event listener
    return () => {
      document.removeEventListener('keydown', handleKeyDown);
    };
  }, [keyboardShortcuts]);

  const currentPageDisplay = pageNum + 1;
  const totalPages = Math.ceil(frameCount / imagesPerPage);  

  return (
    <div className="d-flex justify-content-between align-items-center p-3 border-top" ref={footerRef}>
      <Button 
        variant="outline-primary" 
        onClick={prevPage} 
        disabled={pageNum === 0}
        className="d-flex align-items-center"
      >
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="me-2">
          <polyline points="15,18 9,12 15,6"/>
        </svg>
        Previous
      </Button>
      
      <div className="d-flex align-items-center">
        <span className="text-muted me-2">Page</span>
        <span className="fw-bold">{currentPageDisplay}</span>
        <span className="text-muted mx-1">of</span>
        <span className="fw-bold">{totalPages}</span>
        <span className="text-muted ms-2">({frameCount} images total)</span>
      </div>
      
      <Button 
        variant="outline-primary" 
        onClick={nextPage} 
        disabled={(pageNum + 1) * imagesPerPage >= frameCount}
        className="d-flex align-items-center"
      >
        Next
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="ms-2">
          <polyline points="9,18 15,12 9,6"/>
        </svg>
      </Button>
    </div>
  );
}
