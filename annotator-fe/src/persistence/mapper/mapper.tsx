import { UserRequest } from '../model/requests';
import { Project, Priority, LongUser, LongTeam, Label, Annotation } from '../model/data';
import {
  ProjectApiResponse,
  ShortTeamApiResponse,
  LongTeamApiResponse,
  UserApiResponse,
  LabelApiResponse,
  FrameCountApiResponse,
  AnnotationApiResponse
} from '../model/api-responses';

export function mapProjectResponse(project: ProjectApiResponse<ShortTeamApiResponse>): Project {
  return {
    id: project.id,
    projectName: project.projectName,
    logFileName: project.logFileName,
    deadline: project.deadline,
    priority: project.priority,
    team: project.team
      ? {
          id: project.team.id,
          name: project.team.name
        }
      : null
  };
}

export function mapProjectResponses(data: ProjectApiResponse<LongTeamApiResponse>[]): Project[] {
  return data.map((project) => ({
    id: project.id,
    projectName: project.projectName,
    logFileName: project.logFileName,
    deadline: project.deadline,
    priority: project.priority,
    team: project.team
      ? {
          id: project.team.id,
          name: project.team.name,
          leader: project.team.leader
        }
      : null
  }));
}

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

export function mapUserResponse(data: UserApiResponse[]): LongUser[] {
  return data.map((user) => ({
    id: user.id,
    firstName: user.firstName,
    lastName: user.lastName,
    username: user.username,
    role: user.role,
    team: user.team ? { id: user.team.id, name: user.team.name } : null
  }));
}

export function mapPriorityResponse(data: string[]): Priority[] {
  return data.map((priority) => ({ name: priority }));
}

export function mapUserRequest(data: LongUser): UserRequest {
  return {
    firstName: data.firstName,
    lastName: data.lastName,
    username: data.username,
    teamId: data.team?.id,
    role: data.role
  };
}

export function mapRoles(data: string[]): string[] {
  return data.map((role) => role);
}

export function mapLabels(data: LabelApiResponse[]): Label[] {
  return data.map((label) => ({
    id: label.id,
    label: label.labelName,
    color: label.color
  }));
}

export function mapFrameCount(data: FrameCountApiResponse): number {
  return data.count;
}

export function mapAnnotations(data: AnnotationApiResponse[]): Annotation[] {
  return data.map((annotation) => ({
    frameId: annotation.frameId,
    labelId: annotation.labelId
  }));
}

export function mapLabel(data: LabelApiResponse): Label {
  return {
    id: data.id,
    label: data.labelName,
    color: data.color
  };
}
