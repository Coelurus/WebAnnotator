import React, { useState } from 'react';
import { Label, Project } from '../../persistence/model/data';
import { trainAI } from './ai/train-ai';
import { ToastParams } from '../../notifications/toasts';
import Form from 'react-bootstrap/Form';
import Button from 'react-bootstrap/Button';
import Collapse from 'react-bootstrap/Collapse';
import { LabelRequest } from '../../persistence/model/requests';
import { postCreateLabel } from '../../persistence/requests/poster';

interface AnnotatorHeaderProps {
  imageSize: number;
  setImageSize: React.Dispatch<React.SetStateAction<number>>;
  currentLabel: Label | undefined;
  setCurrentLabel: React.Dispatch<React.SetStateAction<Label | undefined>>;
  labels: Label[];
  setLabels: React.Dispatch<React.SetStateAction<Label[]>>;
  project: Project;
  headerRef: React.RefObject<HTMLDivElement>;
  setToastMessage: React.Dispatch<React.SetStateAction<ToastParams | null>>;
}

export default function AnnotatorHeader({
  imageSize,
  setImageSize,
  currentLabel,
  setCurrentLabel,
  labels,
  setLabels,
  project,
  headerRef,
  setToastMessage
}: AnnotatorHeaderProps) {
  const [newLabel, setNewLabel] = React.useState<LabelRequest>({});
  const [showSettings, setShowSettings] = useState(false);

  const handleSliderChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setImageSize(Number(event.target.value));
  };

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

  const handleAddLabel = () => {
    if (!newLabel.labelName || newLabel.labelName.length === 0) {
      setToastMessage({
        header: 'Error creating label',
        body: `Cannot create empty label.`,
        variant: 'danger'
      });
    } else {
      postCreateLabel(newLabel, (message) =>
        setToastMessage({ header: 'Error creating label', body: message, variant: 'danger' })
      ).then((createdLabel) => {
        if (!createdLabel) return;
        setLabels([...labels, createdLabel]);
        setCurrentLabel(createdLabel);
        setNewLabel({});
      });
    }
  };

  const handleLabelInputFieldChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNewLabel({ ...newLabel, [event.target.name]: event.target.value });
  };

  return (
    <div ref={headerRef} className="p-3 border rounded">
      <div className="d-flex align-items-center justify-content-between">
        <h5 className="m-0">{project ? project.projectName : 'No project found'}</h5>
        <select
          className="form-select w-auto ms-2"
          name="label"
          id="label-select"
          value={currentLabel?.label || ''}
          onChange={handleLabelChange}
        >
          {labels.map((label) => (
            <option
              value={label.label}
              key={'label_' + label.label}
              data-label-id={label.id}
              data-label-name={label.label}
              data-label-color={label.color}
            >
              {label.label}
            </option>
          ))}
        </select>
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
              value={newLabel.color ?? '#563d7c'}
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
