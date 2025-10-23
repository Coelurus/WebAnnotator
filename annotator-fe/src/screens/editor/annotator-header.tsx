import React from 'react';
import { Label, Project, PredictionSegment } from '../../persistence/model/data';
import { trainAI } from './ai/train-ai';
import { predictAI } from './ai/predict-ai';
import { exportData } from './export/export-data';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Collapse from 'react-bootstrap/Collapse';
import Modal from 'react-bootstrap/Modal';
import { LabelRequest } from '../../persistence/model/requests';
import { postCreateLabel } from '../../persistence/requests/poster';
import { LabelApiResponse } from '../../persistence/model/api-responses';
import { mapLabel } from '../../persistence/mapper/mapper';
import toast from 'react-hot-toast';
import { Link } from 'react-router-dom';

/**
 * Interface for the properties of the AnnotatorHeader component.
 */
export interface AnnotatorHeaderProps {
  /**
   * Current size of the image to be displayed.
   */
  imageSize: number;
  /**
   * Function to set the size of the image.
   */
  setImageSize: React.Dispatch<React.SetStateAction<number>>;
  /**
   * Currently selected label.
   */
  currentLabel: Label | undefined;
  /**
   * Function to set the currently selected label.
   */
  setCurrentLabel: React.Dispatch<React.SetStateAction<Label | undefined>>;
  /**
   * List of all available labels.
   */
  labels: Label[];
  /**
   * Function to set the list of labels.
   */
  setLabels: React.Dispatch<React.SetStateAction<Label[]>>;
  /**
   * Current project being edited.
   */
  project: Project;
  /**
   * Callback for handling prediction results.
   */
  onPredict?: (segments: PredictionSegment[]) => void;
  /**
   * Reference to the header element for accessing its size.
   */
  headerRef: React.RefObject<HTMLDivElement>;
}

/**
 * AnnotatorHeader component provides controls for selecting labels, adjusting image size, and managing project settings.
 * 
 * @param props The properties for the AnnotatorHeader component.
 * @returns The rendered header with controls for label selection, image size adjustment, and project settings
 */
export default function AnnotatorHeader({
  imageSize,
  setImageSize,
  currentLabel,
  setCurrentLabel,
  labels,
  setLabels,
  project,
  onPredict,
  headerRef
}: AnnotatorHeaderProps) {
  
  // Luminance calculation constants for contrast color determination
  const LUMINANCE_WEIGHTS = {
    RED: 0.299,
    GREEN: 0.587,
    BLUE: 0.114
  } as const;
  
  /**
   * Generates a random hex color for new labels
   */
  const generateRandomColor = () => {
    const getRandomHex = () => Math.floor(Math.random() * 256).toString(16).padStart(2, '0');
    return `#${getRandomHex()}${getRandomHex()}${getRandomHex()}`;
  };

  // Contrast threshold for determining text color
  const CONTRAST_THRESHOLD = 0.5;
  // Maximum value for RGB color channels
  const RGB_MAX = 255;
  // State to manage inputting and creating a new label
  const [newLabel, setNewLabel] = React.useState<LabelRequest>({ color: generateRandomColor() });
  // State to manage the visibility of settings bar
  const [showSettings, setShowSettings] = React.useState(false);
  // State to manage the visibility of info modal
  const [showInfoModal, setShowInfoModal] = React.useState(false);
  // State to track the previously selected label for Q shortcut toggle
  const [lastUsedLabel, setLastUsedLabel] = React.useState<Label | undefined>(undefined);
  // Ref for the label select dropdown
  const labelSelectRef = React.useRef<HTMLSelectElement>(null);
  // Ref for the new label input field
  const newLabelInputRef = React.useRef<HTMLInputElement>(null);

  /**
   * Keyboard shortcut key constants
   */
  const KEYS = {
    TOGGLE_SETTINGS: 's',
    FOCUS_LABEL_SELECT: 'l',
    CLOSE: 'escape',
    NEW_LABEL: 'n',
    HELP: 'h',
    TOGGLE_LAST_LABEL: 'q',
    TRAIN_AI: 'ctrl+a',
    EXPORT_DATA: 'ctrl+e',
    PREDICT: 'ctrl+p',
  } as const;

  /**
   * Determines the best text color (black or white) for readability on a given background color
   */
  const getContrastColor = (hexColor: string): string => {
    const color = hexColor.replace('#', '');
    
    // Convert hex to RGB
    const r = parseInt(color.substring(0, 2), 16);
    const g = parseInt(color.substring(2, 4), 16);
    const b = parseInt(color.substring(4, 6), 16);
    
    // Calculate relative luminance using standard weights
    const luminance = (LUMINANCE_WEIGHTS.RED * r + LUMINANCE_WEIGHTS.GREEN * g + LUMINANCE_WEIGHTS.BLUE * b) / RGB_MAX;
    
    // Return black for light colors, white for dark colors
    return luminance > CONTRAST_THRESHOLD ? '#000000' : '#FFFFFF';
  };

  /**
   * Toggles between current label and last used label
   */
  const toggleLastLabel = () => {
    // Do nothing if no current label or no last used label
    if (!currentLabel || !lastUsedLabel) {
      return;
    }
    
    // Do nothing if there's only one label total
    if (labels.length < 2) {
      return;
    }
    
    // Switch current and last used labels
    const oldCurrent = {...currentLabel};
    const oldLastUsed = {...lastUsedLabel};
    setCurrentLabel(oldLastUsed);
    setLastUsedLabel(oldCurrent);
  };

  /**
   * Configuration object for keyboard shortcuts
   */
  const keyboardShortcuts = React.useMemo(() => ({
    [KEYS.TOGGLE_SETTINGS]: () => setShowSettings(!showSettings),
    [KEYS.FOCUS_LABEL_SELECT]: () => labelSelectRef.current?.focus(),
    [KEYS.CLOSE]: () => {
      // If help modal is open, close it first
      if (showInfoModal) {
        setShowInfoModal(false);
      }
      // If there's a focused element -> blur it
      else if (document.activeElement && document.activeElement !== document.body) {
        (document.activeElement as HTMLElement).blur();
      }
      // If nothing is focused and settings are open, close settings
      else if (showSettings) {
        setShowSettings(false);
      }
    },
    [KEYS.HELP]: () => setShowInfoModal(!showInfoModal),
    [KEYS.NEW_LABEL]: () => {
      if (!showSettings) {
        setShowSettings(true);
      }
      // Set random color
      setNewLabel({ labelName: newLabel.labelName, color: generateRandomColor() });
      // Use setTimeout to ensure the input field is rendered after settings panel opens
      setTimeout(() => newLabelInputRef.current?.focus(), 0);
    },
    [KEYS.TOGGLE_LAST_LABEL]: () => toggleLastLabel(),
    [KEYS.TRAIN_AI]: () => trainAI(project.id),
    [KEYS.EXPORT_DATA]: () => exportData(project.id, project.projectName),
    [KEYS.PREDICT]: () => predictAI(project.id, (data) => onPredict?.(data.segments)),
  }), [showSettings, showInfoModal, project.id, project.projectName, labels, toggleLastLabel, onPredict]);

  /**
   * Effect to handle keyboard shortcuts
   */
  React.useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      const key = event.key.toLowerCase();
      
      // Handle Ctrl combinations regardless of focused element
      if (event.ctrlKey) {
        const ctrlKey = `ctrl+${key}`;
        const ctrlAction = keyboardShortcuts[ctrlKey as keyof typeof keyboardShortcuts];
        if (ctrlAction) {
          event.preventDefault();
          ctrlAction();
          return;
        }
      }
      
      // Handle regular shortcuts only if not typing in an input field
      if (event.target instanceof Element &&
          (!['INPUT', 'TEXTAREA', 'SELECT'].includes(event.target.tagName) ||
          key === KEYS.CLOSE)
        ) {
        
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
  }, [keyboardShortcuts]); // Dependency on the shortcuts object

  /**
   * Handles the change event for the slider input to adjust image size.
   * 
   * @param event Change event from the slider input to adjust image size - user slides the slider to resize images.
   */
  const handleSliderChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setImageSize(Number(event.target.value));
  };

  /**
   * Handles the change event for the label selection dropdown.
   * 
   * @param event Change event from the label selection dropdown - user selects another label from the dropdown.
   */
  const handleLabelChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const selectedLabelId = Number(event.target.selectedOptions[0].getAttribute('data-label-id'));
    const selectedLabelName = event.target.selectedOptions[0].getAttribute('data-label-name');
    const selectedLabelColor = event.target.selectedOptions[0].getAttribute('data-label-color');
    if (selectedLabelName && selectedLabelColor) {
      // Store current label as last used before changing
      if (currentLabel) {
        setLastUsedLabel(currentLabel);
      }
      setCurrentLabel({
        id: selectedLabelId,
        label: selectedLabelName,
        color: selectedLabelColor
      });
    }
  };

  /**
   * Handles the addition of a new label.
   * If the new label name is empty, it shows an error toast.
   */
  const handleAddLabel = () => {
    if (!newLabel.labelName || newLabel.labelName.length === 0) {
      toast.error('Cannot create empty label');
    } else {
      postCreateLabel(newLabel).then((createdLabel: LabelApiResponse) => {
        if (!createdLabel) return;
        setLabels([...labels, mapLabel(createdLabel)]);

        // Store current label as last used before changing to new label
        if (currentLabel) {
          setLastUsedLabel(currentLabel);
        }
        setCurrentLabel(mapLabel(createdLabel));
        setNewLabel({ color: generateRandomColor() });
      });
    }
  };

  /**
   * Handles the change event for the label input field.
   * 
   * @param event Change event from the label input field - user types in a new label name or selects a color.
   */
  const handleLabelInputFieldChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNewLabel({ ...newLabel, [event.target.name]: event.target.value });
  };

  /**
   * Handles key down events for the new label input field.
   * Triggers label creation when Enter is pressed.
   * 
   * @param event Keyboard event from the new label input field.
   */
  const handleNewLabelKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      event.preventDefault();
      handleAddLabel();
    }
  };

  return (
    <div ref={headerRef} className="p-3 border rounded">
      <div className="d-flex align-items-center justify-content-between">
        <div className="d-flex align-items-center">
          <Link to="../../" className="p-2 d-inline-block me-2">
            <svg width="24" height="48" viewBox="0 0 60 120" xmlns="http://www.w3.org/2000/svg">
              <path d="M30,0 L60,20 L60,120 L0,120 L0,20 Z" fill="#f7f7f7" stroke="#333" strokeWidth="3" />
              <circle cx="30" cy="10" r="5" fill="#fff" stroke="#333" strokeWidth="2" />
              <text x="30" y="75" textAnchor="middle" fontFamily="Arial, sans-serif" fontSize="36" fill="#333" dominantBaseline="middle">
                A
              </text>
            </svg>
          </Link>
          <h5 className="m-0">{project ? project.projectName : 'No project found'}</h5>
        </div>
        <div className="d-flex align-items-center">
          <Form.Select
            ref={labelSelectRef}
            className="form-select"
            name="label"
            id="label-select"
            value={currentLabel?.id ?? ''}
            onChange={handleLabelChange}
          >
            {labels.map((label) => (
              <option
                value={label.id}
                key={'label_' + label.id}
                data-label-id={label.id}
                data-label-name={label.label}
                data-label-color={label.color}
                style={{
                  backgroundColor: label.color,
                  color: getContrastColor(label.color)
                }}
              >
                {label.label}
              </option>
            ))}
          </Form.Select>
          {currentLabel && (
            <div 
              className="ms-2 rounded-circle border"
              style={{
                width: '20px',
                height: '20px',
                backgroundColor: currentLabel.color,
                minWidth: '20px'
              }}
              title={`Color: ${currentLabel.color}`}
            />
          )}
        </div>
        <Button variant="secondary" className="ms-2" onClick={() => setShowSettings(!showSettings)}>
          {showSettings ? 'Hide' : 'Show'} Settings
        </Button>
        <Button variant="outline-info" className="ms-2" onClick={() => setShowInfoModal(true)} title="Help & Shortcuts">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
            <circle cx="12" cy="12" r="10"/>
            <path d="M9.09 9a3 3 0 0 1 5.83 1c0 2-3 3-3 3"/>
            <line x1="12" y1="17" x2="12.01" y2="17"/>
          </svg>
        </Button>
      </div>
      <Collapse in={showSettings}>
        <div>
          <div className="mt-3">
            <Form.Label htmlFor="image-size-slider">Image Size</Form.Label>
            <Form.Range
              id="image-size-slider"
              min="30"
              max="200"
              value={imageSize}
              onChange={handleSliderChange}
            />
          </div>

          <div className="mt-3 d-flex">
            <Form.Control
              ref={newLabelInputRef}
              type="text"
              placeholder="New label"
              onChange={handleLabelInputFieldChange}
              onKeyDown={handleNewLabelKeyDown}
              value={newLabel.labelName ?? ''}
              name="labelName"
              className="me-2"
            />
            <Form.Control
              type="color"
              title="Choose color for your label"
              onChange={handleLabelInputFieldChange}
              name="color"
              value={newLabel.color ?? generateRandomColor()}
              className="me-2"
            />
            <Button variant="primary" onClick={handleAddLabel}>
              Add Label
            </Button>
          </div>

          <div className="mt-3 d-flex gap-2">
            <Button variant="success" onClick={() => trainAI(project.id)}>
              Train AI
            </Button>
            <Button variant="info" onClick={() => predictAI(project.id, (data) => onPredict?.(data.segments))}>
              Predict
            </Button>
            <Button variant="primary" onClick={() => exportData(project.id, project.projectName)}>
              Export Data
            </Button>
          </div>
        </div>
      </Collapse>

      {/* Info Modal */}
      <Modal show={showInfoModal} onHide={() => setShowInfoModal(false)} size="lg">
        <Modal.Header closeButton>
          <Modal.Title>Annotation Help & Keyboard Shortcuts</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="row">
            <div className="col-md-6">
              <h5>How to set up</h5>
              <ul>
                <li><strong>Select a Label:</strong> Choose from the dropdown</li>
                <li><strong>Create label:</strong> Fill in name and choose color</li>
                <li><strong>Resize:</strong> Choose frame size using slider</li>
              </ul>

              <h5 className="mt-4">How to Annotate</h5>
              <ul>
                <li><strong>Apply labels:</strong> Left-click a frame and drag to another. All annotations in the selected range will be annotated with selected label.</li>
                <li><strong>Erase labels:</strong> Right-click a frame and drag to another. Results into erasing all annotations in the selected range.</li>
                <li><strong>Auto page flip:</strong> When last frame on a page is contained in the selection (either annotating or erasing) it is automatically switched to the next page.</li>
              </ul>

              <h5 className="mt-4">When finished</h5>
              <ul>
                <li><strong>Send to AI:</strong> Send annotated frames to the AI model for learning.</li>
                <li><strong>Export:</strong> Export the annotated data for external use.</li>
              </ul>
            </div>
            <div className="col-md-6">
              <h5>Keyboard Shortcuts</h5>
              <div className="mb-3">
                <h6>Navigation</h6>
                <ul className="list-unstyled">
                  <li><kbd>A</kbd> - Previous page</li>
                  <li><kbd>D</kbd> - Next page</li>
                </ul>
              </div>
              
              <div className="mb-3">
                <h6>Controls</h6>
                <ul className="list-unstyled">
                  <li><kbd>S</kbd> - Toggle Settings</li>
                  <li><kbd>L</kbd> - Focus Label dropdown</li>
                  <li><kbd>N</kbd> - New label (random color)</li>
                  <li><kbd>Q</kbd> - Toggle between current and last used label</li>
                  <li><kbd>ESC</kbd> - Unfocus/Close settings</li>
                  <li><kbd>H</kbd> - Open this help window</li>
                </ul>
              </div>
              
              <div className="mb-3">
                <h6>Actions</h6>
                <ul className="list-unstyled">
                  <li><kbd>Ctrl+A</kbd> - Train AI</li>
                  <li><kbd>Ctrl+E</kbd> - Export Data</li>
                  <li><kbd>Ctrl+P</kbd> - Predict</li>
                </ul>
              </div>
            </div>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowInfoModal(false)}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
}
