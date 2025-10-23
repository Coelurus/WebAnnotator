import { AIModelUpdateResponse } from '../../../persistence/model/data';
import { getAIAnnotatedResult } from '../../../persistence/requests/poster';
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
 */
export const trainAI = async (projectId: number) => {
  const requestPromise = getAIAnnotatedResult(projectId)
    .then((data: AIModelUpdateResponse) => {
      console.log('AI Training Result:', data);
      return data;
    })
    .catch((error) => {
      console.log(error);
      throw new Error('Training failed');
    });

  return toast.promise(requestPromise, {
    loading: 'Training AI, please wait...',
    success: (data: AIModelUpdateResponse) => `Training completed: ${data.status} (Accuracy: ${(data.accuracy * 100).toFixed(2)}%, Projects: ${data.projects})`,
    error: 'Training failed'
  });
};
