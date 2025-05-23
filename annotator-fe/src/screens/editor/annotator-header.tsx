import React, { useState } from 'react';
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

interface AnnotatorHeaderProps {
  imageSize: number;
  setImageSize: React.Dispatch<React.SetStateAction<number>>;
  currentLabel: Label | undefined;
  setCurrentLabel: React.Dispatch<React.SetStateAction<Label | undefined>>;
  labels: Label[];
  setLabels: React.Dispatch<React.SetStateAction<Label[]>>;
  project: Project;
  headerRef: React.RefObject<HTMLDivElement>;
}

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
  const DEFAULT_COLOR_OPTION = '#563d7c';

  const [newLabel, setNewLabel] = React.useState<LabelRequest>({ color: DEFAULT_COLOR_OPTION });
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
