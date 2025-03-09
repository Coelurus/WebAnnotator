import React from 'react';
import { request } from '../../security/auth';
import { Label, Project } from '../../persistence/model/data';
import { trainAI } from './ai/train-ai';
import { ToastParams } from '../../notifications/toasts';

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
  const [newLabel, setNewLabel] = React.useState<string>('');

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
    if (newLabel.length === 0) {
      setToastMessage({
        header: 'Error creating label',
        body: `Cannot create empty label.`,
        variant: 'danger'
      });
    } else {
      request('POST', `/api/labels/${newLabel}`)
        .then((response) => {
          setLabels([...labels, response.data]);
          setCurrentLabel(response.data);
        })
        .catch(() => {
          setToastMessage({
            header: 'Error creating label',
            body: `Label with name ${newLabel} already exists.`,
            variant: 'danger'
          });
        });
      setNewLabel('');
    }
  };

  const handleLabelInputFieldChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setNewLabel(event.target.value);
  };

  return (
    <>
      <div ref={headerRef}>
        <h1>{project ? project.projectName : 'No project found'}</h1>

        <div className="slider-container">
          <label htmlFor="image-size-slider">Image Size: {imageSize}px</label>
          <input
            type="range"
            id="image-size-slider"
            min="30"
            max="200"
            value={imageSize}
            onChange={handleSliderChange}
          />
        </div>

        <select
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

        <input
          type="text"
          id="new-label-text-input"
          placeholder="New label"
          onChange={handleLabelInputFieldChange}
          value={newLabel}
        />

        <button onClick={handleAddLabel}>Add label</button>
        <button onClick={() => trainAI(project.id, labels)}>Train AI</button>
      </div>
    </>
  );
}
