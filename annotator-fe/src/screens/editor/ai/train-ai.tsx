import { Label, PredictionTriple } from '../../../persistence/model/data';
import { getAIAnnotatedResult } from '../../../persistence/requests/fetcher';
import toast from 'react-hot-toast';

function normalizeColor(color: string): string {
  const temp = document.createElement('div');
  temp.style.color = color;
  document.body.appendChild(temp);

  const computedColor = getComputedStyle(temp).color;
  document.body.removeChild(temp);

  return computedColor;
}

/**
 * Function to train the AI model for a specific project using the provided labels.
 * It sends a POST request to the server and updates the UI based on the response.
 *
 * @param projectId The ID of the project to train the AI model for.
 * @param labels The list of labels used for training.
 */
export const trainAI = async (projectId: number, labels: Label[]) => {
  const requestPromise = getAIAnnotatedResult(projectId)
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

      return data;
    })
    .catch((error) => {
      console.log(error);
      throw new Error('Training failed');
    });

  return toast.promise(requestPromise, {
    loading: 'Training AI, please wait...',
    success: 'Training completed successfully!',
    error: 'Training failed'
  });
};
