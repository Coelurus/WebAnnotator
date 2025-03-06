import { UserRequest } from '../model/requests';
import { PriorityResponse, ProjectResponse, TeamResponse, UserResponse } from '../model/responses';

export function mapProjectResponse(project: any): ProjectResponse {
  return {
    id: project.id,
    projectName: project.project_name,
    logFileName: project.log_file_name,
    deadline: project.dead_line,
    priority: project.priority,
    team: {
      id: project.team.id,
      name: project.team.name
    }
  };
}

export function mapProjectResponses(data: any[]): ProjectResponse[] {
  return data.map((project) => ({
    id: project.id,
    projectName: project.project_name,
    logFileName: project.log_file_name,
    deadline: project.dead_line,
    priority: project.priority,
    team: {
      id: project.team.id,
      name: project.team.name,
      leader: project.team.leader
    }
  }));
}

export function mapTeamResponse(data: any[]): TeamResponse[] {
  return data.map((team) => ({
    id: team.id,
    name: team.name,
    leader: team.leader
      ? {
        id: team.leader.id,
        firstName: team.leader.first_name,
        lastName: team.leader.last_name
      }
      : null
  }));
}

export function mapUserResponse(data: any[]): UserResponse[] {
  return data.map((user) => ({
    id: user.id,
    firstName: user.first_name,
    lastName: user.last_name,
    username: user.username,
    role: user.role,
    team: user.team ? { id: user.team.id, name: user.team.name } : null
  }));
}

export function mapPriorityResponse(data: string[]): PriorityResponse[] {
  return data.map((priority) => ({ name: priority }));
}

export function mapUserRequest(data: UserResponse): UserRequest {
  return {
    firstName: data.firstName,
    lastName: data.lastName,
    username: data.username,
    teamId: data.team?.id,
    role: data.role
  };
}

export function mapRoles(data: any[]): string[] {
  return data.map((role) => role)
}