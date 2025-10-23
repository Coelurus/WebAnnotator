import { PredictionResponse } from '../../../persistence/model/data';
import { getAIPrediction } from '../../../persistence/requests/poster';
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
 * Function to predict gestures for a specific project using the AI model.
 * It sends a POST request to the server and returns the predicted segments.
 *
 * @param projectId The ID of the project to predict gestures for.
 * @param onSuccess Callback to handle the prediction response.
 * @returns A promise that resolves to the PredictionResponse from the server.
 */
export const predictAI = async (projectId: number, onSuccess?: (data: PredictionResponse) => void): Promise<PredictionResponse> => {
  const requestPromise = getAIPrediction(projectId)
    .then((data: PredictionResponse) => {
      console.log('AI Prediction Result:', data);
      if (onSuccess) {
        onSuccess(data);
      }
      return data;
    })
    .catch((error: any) => {
      console.log(error);
      throw new Error('Prediction failed');
    });

  return toast.promise(requestPromise, {
    loading: 'Predicting gestures, please wait...',
    success: (data: PredictionResponse) => `Prediction completed: ${data.segments.length} segments found`,
    error: 'Prediction failed'
  });
};