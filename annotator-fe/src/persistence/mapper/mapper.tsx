import { ProjectRequest, TeamRequest, UserRequest } from '../model/requests';
import { Project, Priority, LongUser, LongTeam, Label, Annotation, Progress } from '../model/data';
import {
  ProjectApiResponse,
  ShortTeamApiResponse,
  LongTeamApiResponse,
  LongUserApiResponse,
  LabelApiResponse,
  FrameCountApiResponse,
  AnnotationApiResponse,
  ProgressApiResponse,
  PriorityApiResponse
} from '../model/api-responses';

/**
 * Maps a ProjectApiResponse with ShortTeamApiResponse to a Project object.
 * 
 * @param project ProjectApiResponse with ShortTeamApiResponse
 * @returns Mapped Project object
 */
export function mapProjectResponse(project: ProjectApiResponse<ShortTeamApiResponse>): Project {
  return {
    id: project.id,
    projectName: project.projectName,
    logFileName: project.logFileName,
    deadline: project.deadline,
    priority: project.priority,
    progress: project.progress,
    team: project.team
      ? {
          id: project.team.id,
          name: project.team.name
        }
      : null
  };
}

/**
 * Maps an array of ProjectApiResponse with LongTeamApiResponse to an array of Project objects.
 * 
 * @param data Array of ProjectApiResponse with LongTeamApiResponse 
 * @returns Array of mapped Project objects
 */
export function mapProjectResponses(data: ProjectApiResponse<LongTeamApiResponse>[]): Project[] {
  return data.map((project) => ({
    id: project.id,
    projectName: project.projectName,
    logFileName: project.logFileName,
    deadline: project.deadline,
    priority: project.priority,
    progress: project.progress,
    team: project.team
      ? {
          id: project.team.id,
          name: project.team.name,
          leader: project.team.leader
        }
      : null
  }));
}

/**
 * Maps an array of LongTeamApiResponse to an array of LongTeam objects.
 * 
 * @param data Array of LongTeamApiResponse containing team details
 * @returns Array of mapped LongTeam objects
 */
export function mapTeamResponse(data: LongTeamApiResponse[]): LongTeam[] {
  return data.map((team) => ({
    id: team.id,
    name: team.name,
    leader: team.leader
      ? {
          id: team.leader.id,
          firstName: team.leader.firstName,
          lastName: team.leader.lastName
        }
      : null
  }));
}

/**
 * Maps an array of UserApiResponse to an array of LongUser objects.
 * 
 * @param data Array of UserApiResponse containing user details
 * @returns Array of mapped LongUser objects
 */
export function mapUserResponse(data: LongUserApiResponse[]): LongUser[] {
  return data.map((user) => ({
    id: user.id,
    firstName: user.firstName,
    lastName: user.lastName,
    username: user.username,
    role: user.role,
    team: user.team ? { id: user.team.id, name: user.team.name } : null
  }));
}

/**
 * Maps a UserApiResponse to a LongUser object.
 * 
 * @param data UserApiResponse containing priority details
 * @returns Mapped LongUser object
 */
export function mapPriorityResponse(data: PriorityApiResponse[]): Priority[] {
  return data.map((priority) => ({
    name: priority.name,
    value: priority.value
  }));
}

/**
 * Maps a LongUser object to a UserRequest object.
 * 
 * @param data LongUser object containing user details
 * @returns 
 */
export function mapUserRequest(data: LongUser): UserRequest {
  return {
    firstName: data.firstName,
    lastName: data.lastName,
    username: data.username,
    teamId: data.team?.id,
    role: data.role
  };
}

/**
 * Maps a LongTeam object to a TeamRequest object.
 * 
 * @param data LongTeam object containing team details
 * @returns Mapped TeamRequest object
 */
export function mapTeamRequest(data: LongTeam): TeamRequest {
  return {
    name: data.name,
    leaderId: data.id
  };
}

/**
 * Maps a Project object to a ProjectRequest object.
 * 
 * @param data Project object containing project details
 * @returns Mapped ProjectRequest object
 */
export function mapProjectRequest(data: Project): ProjectRequest {
  return {
    projectName: data.projectName,
    deadline: data.deadline,
    priority: data.priority,
    progress: data.progress,
    teamId: data.team?.id
  };
}

/**
 * Maps an array of roles (strings) to an array of strings.
 * 
 * @param data List of roles as strings
 * @returns List of roles as strings
 */
export function mapRoles(data: string[]): string[] {
  return data.map((role) => role);
}

/**
 * Maps an array of LabelApiResponse to an array of Label objects.
 * 
 * @param data Array of LabelApiResponse containing label details
 * @returns Array of mapped Label objects
 */
export function mapLabels(data: LabelApiResponse[]): Label[] {
  return data.map((label) => ({
    id: label.id,
    label: label.labelName,
    color: label.color
  }));
}

/**
 * Maps a FrameCountApiResponse to a number representing the frame count.
 * 
 * @param data FrameCountApiResponse containing the frame count
 * @returns Number representing the frame count
 */
export function mapFrameCount(data: FrameCountApiResponse): number {
  return data.count;
}

/**
 * Maps an array of AnnotationApiResponse to an array of Annotation objects.
 * 
 * @param data Array of AnnotationApiResponse containing annotation details
 * @returns Array of mapped Annotation objects
 */
export function mapAnnotations(data: AnnotationApiResponse[]): Annotation[] {
  return data.map((annotation) => ({
    frameId: annotation.frameId,
    labelId: annotation.labelId
  }));
}

/**
 * Maps a LabelApiResponse to a Label object.
 * 
 * @param data LabelApiResponse containing label details
 * @returns Mapped Label object
 */
export function mapLabel(data: LabelApiResponse): Label {
  return {
    id: data.id,
    label: data.labelName,
    color: data.color
  };
}

/**
 * Maps an array of ProgressApiResponse to an array of Progress objects.
 * 
 * @param data Array of ProgressApiResponse containing progress details
 * @returns Array of mapped Progress objects
 */
export function mapProgresses(data: ProgressApiResponse[]): Progress[] {
  return data.map((progress) => ({
    value: progress.value,
    name: progress.name
  }));
}
