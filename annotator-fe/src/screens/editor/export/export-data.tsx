import { getExportedData } from '../../../persistence/requests/fetcher';
import toast from 'react-hot-toast';

/**
 * Function to export the annotated project data as a CSV file.
 * Calls the backend API which returns ready-to-download CSV content.
 *
 * @param projectId The ID of the project to export data for.
 * @param projectName The name of the project for the filename.
 */
export const exportData = async (projectId: number, projectName: string) => {
  const requestPromise = getExportedData(projectId)
    .then((csvContent) => {
      downloadCSV(csvContent, `${projectName}_annotated_data.csv`);
      return csvContent;
    })
    .catch((error) => {
      console.error('Error exporting data:', error);
      throw new Error('Failed to export data. Please try again.');
    });

  return toast.promise(requestPromise, {
    loading: 'Exporting, please wait...',
    success: 'Data exported successfully!',
    error: 'Failed to export data. Please try again.'
  });
};

/**
 * Download CSV content as a file
 * @param csvContent The CSV content as a string
 * @param filename The name of the file to download
 */
const downloadCSV = (csvContent: string, filename: string): void => {
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement('a');
  
  if (link.download !== undefined) {
    const url = URL.createObjectURL(blob);
    link.setAttribute('href', url);
    link.setAttribute('download', filename);
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  }
};