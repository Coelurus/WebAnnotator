import React from 'react';
import { Label, Project } from '../../persistence/model/data';
import { trainAI } from './ai/train-ai';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Collapse from 'react-bootstrap/Collapse';
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
  headerRef
}: AnnotatorHeaderProps) {
  // Default color option for new labels
  const DEFAULT_COLOR_OPTION = '#563d7c';
  // State to manage inputting and creating a new label
  const [newLabel, setNewLabel] = React.useState<LabelRequest>({ color: DEFAULT_COLOR_OPTION });
  // State to manage the visibility of settings bar
  const [showSettings, setShowSettings] = React.useState(false);

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

        setCurrentLabel(mapLabel(createdLabel));
        setNewLabel({});
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
        <Form.Select
          className="form-select w-auto ms-2"
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
            >
              {label.label}
            </option>
          ))}
        </Form.Select>
        <Button variant="secondary" className="ms-2" onClick={() => setShowSettings(!showSettings)}>
          {showSettings ? 'Hide' : 'Show'} Settings
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
              type="text"
              placeholder="New label"
              onChange={handleLabelInputFieldChange}
              value={newLabel.labelName ?? ''}
              name="labelName"
              className="me-2"
            />
            <Form.Control
              type="color"
              title="Choose color for your label"
              onChange={handleLabelInputFieldChange}
              name="color"
              value={newLabel.color ?? DEFAULT_COLOR_OPTION}
              className="me-2"
            />
            <Button variant="primary" onClick={handleAddLabel}>
              Add Label
            </Button>
          </div>

          <Button variant="success" className="mt-3" onClick={() => trainAI(project.id, labels)}>
            Train AI
          </Button>
        </div>
      </Collapse>
    </div>
  );
}
