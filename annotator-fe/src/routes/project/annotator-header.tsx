import React from 'react';
import { request } from '../../security/auth';
import { Label, ProjectResponse } from '../../persistence/model/responses';
import { trainAI } from './ai/train-ai';

interface AnnotatorHeaderProps {
  imageSize: number;
  setImageSize: React.Dispatch<React.SetStateAction<number>>;
  currentLabel: Label | undefined;
  setCurrentLabel: React.Dispatch<React.SetStateAction<Label|undefined>>;
  labels: Label[];
  setLabels: React.Dispatch<React.SetStateAction<Label[]>>;
  project: ProjectResponse;
}

export default function AnnotatorHeader({
  imageSize,
  setImageSize,
  currentLabel,
  setCurrentLabel,
  labels,
  setLabels,
  project
}: AnnotatorHeaderProps) {
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
    const element = document.getElementById('new-label-text-input') as HTMLInputElement;

    if (element) {
      request('POST', `/api/labels/${element.value}`)
        .then((response) => {
          setLabels([...labels, response.data]);
          setCurrentLabel(response.data);
        })
        .catch(() => {
          alert('Error creating label: ' + element.value + '\nLabel name already exists.');
        });
    } else {
      alert('Issue occurred');
    }
  };

  return (
    <>
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

      <input type="text" id="new-label-text-input" placeholder="New label"></input>
      <button onClick={() => handleAddLabel()}>Add label</button>

      <button onClick={() => trainAI(project.id, labels)}>Train AI</button>
    </>
  );
}
