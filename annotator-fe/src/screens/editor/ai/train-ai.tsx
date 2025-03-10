import axios from 'axios';
import { Label, PredictionTriple } from '../../../persistence/model/data';

export const trainAI = async (projectId: number, labels: Label[]) => {
  try {
    const { data: response } = await axios.post<PredictionTriple[]>(
      `/api//projects/${projectId}/trainAI`
    );
    response.forEach((element) => {
      const imageFrameElement = document.getElementById(`image-frame-indicator-${element.frameId}`);
      const color = labels.filter((label) => label.id == Number(element.label))[0].color;
      if (imageFrameElement) {
        imageFrameElement.textContent = 'X';
        imageFrameElement.style.color = color;
      }
    });

    alert('Training finished!');
  } catch (error) {
    alert('Training failed: ' + error);
  }
};
