import { request } from "../../security/auth";

export function deleteTeamRequest(
    teamId: number,
    onError: (message: string) => void
  ): Promise<void> {
    return request('DELETE', `/api/teams/${teamId}`)
    .then(() => {})
      .catch((error) => {
        console.error(error);
        onError('Error deleting team');
      });
  }

  export function deleteProjectRequest(
    projectId: number,
    onError: (message: string) => void
  ): Promise<void> {
    return request('DELETE', `/api/projects/${projectId}`)
      .then(() => {})
      .catch((error) => {
        console.error(error);
        onError('Error deleting project');
      });
  }