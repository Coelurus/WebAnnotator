import axios from 'axios';
import { Label, PredictionTriple } from '../../../persistence/model/data';
import { request } from '../../../security/auth';
import toast from 'react-hot-toast';

function normalizeColor(color: string): string {
  const temp = document.createElement('div');
  temp.style.color = color;
  document.body.appendChild(temp);

  const computedColor = getComputedStyle(temp).color;
  document.body.removeChild(temp);

  return computedColor;
}

export const trainAI = async (projectId: number, labels: Label[]) => {
  try {
    request('POST', `/api/projects/${projectId}/trainAI`)
      .then((response) => {      
        return response.data
      })
      .then((data: PredictionTriple[]) => {
        
        data.forEach((element) => {
          
          const imageFrameElement = document.getElementById(`image-frame-indicator-${element.frameId}`);
          
          if (imageFrameElement) {          
            
            const color = labels.filter((label) => label.label == element.label)[0]?.color ?? '#333741';            
            const imageElement = document.getElementById(`image-frame-${element.frameId}`)
            
            if (imageElement !== null && getComputedStyle(imageElement).borderColor === "rgb(33, 37, 41)") {
              // Default color
            } else if (imageElement !== null && getComputedStyle(imageElement).borderColor === normalizeColor(color)) {
              imageFrameElement.textContent = '✓';
            } else {
              imageFrameElement.textContent = '✗';
            }
            imageFrameElement.style.color = color;
          }
        });

      })
      .then(() => {
        toast.success('Training completed successfully!');
      })
      .catch(() => {
        throw new Error('Training failed');
      });
      
  } catch (error) {
    console.log(error);
    toast.error('Training failed');
  }
};
