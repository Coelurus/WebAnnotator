import { request } from '../../../security/auth';
import toast from 'react-hot-toast';

/**
 * Interface for exported log data
 */
interface ExportedLogData {
  time: number;
  posX: number;
  posY: number;
  posZ: number;
  label: string;
}

/**
 * Function to export the annotated project data as a CSV file.
 * It fetches the project data from the server and downloads it as a CSV file.
 *
 * @param projectId The ID of the project to export data for.
 * @param projectName The name of the project for the filename.
 */
export const exportData = async (projectId: number, projectName: string) => {
  try {
    const response = await request('GET', `/api/projects/${projectId}/export`);
    const data = response.data;
    
    // Convert data to CSV format
    const csvContent = convertToCSV(data);
    
    // Create and download the CSV file
    downloadCSV(csvContent, `${projectName}_annotated_data.csv`);
    
    toast.success('Data exported successfully!');
  } catch (error) {
    console.error('Error exporting data:', error);
    toast.error('Failed to export data. Please try again.');
  }
};

/**
 * Convert the log data to CSV format
 * @param data Array of log data objects
 * @returns CSV string
 */
const convertToCSV = (data: ExportedLogData[]): string => {
  if (!data || data.length === 0) {
    return 'time,posX,posY,posZ,label\n';
  }
  
  const headers = ['time', 'posX', 'posY', 'posZ', 'label'];
  const csvRows = [headers.join(',')];
  
  data.forEach((row) => {
    const values = headers.map(header => {
      const value = row[header as keyof ExportedLogData];
      // Handle values that might contain commas or quotes
      if (typeof value === 'string' && (value.includes(',') || value.includes('"'))) {
        return `"${value.replace(/"/g, '""')}"`;
      }
      return value;
    });
    csvRows.push(values.join(','));
  });
  
  return csvRows.join('\n');
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