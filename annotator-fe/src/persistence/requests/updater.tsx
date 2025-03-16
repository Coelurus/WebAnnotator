import React from 'react';
import toast from 'react-hot-toast';
import { request } from '../../security/auth';
import { ProjectRequest } from '../model/requests';

export function updateProject(projectId: number, projectData: ProjectRequest): Promise<void> {
  const requestPromise = request('PUT', `/api/projects/${projectId}`, projectData)
    .then(() => {})
    .catch(() => {
      throw new Error('Update project failed');
    });
  return toast.promise(requestPromise, {
    loading: 'Updating project...',
    success: <b>Project updated successfully!</b>,
    error: <b>Could not update project.</b>
  });
}
