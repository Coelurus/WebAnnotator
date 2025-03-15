export interface ProjectApiResponse<TeamResponse> {
  id: number;
  projectName: string;
  logFileName: string;
  deadline: string;
  priority: string;
  progress: string;
  team: TeamResponse | null;
}

export interface LongTeamApiResponse {
  id: number;
  name: string;
  leader: ShortUserApiResponse | null;
}

export interface ShortTeamApiResponse {
  id: number;
  name: string;
}

export interface ShortUserApiResponse {
  id: number;
  firstName: string;
  lastName: string;
}

export interface UserApiResponse {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  role: string;
  team: ShortTeamApiResponse | null;
}

export interface LabelApiResponse {
  id: number;
  labelName: string;
  color: string;
}

export interface FrameCountApiResponse {
  count: number;
}

export interface AnnotationApiResponse {
  projectId: number;
  frameId: number;
  labelId: number;
}

export interface ProgressApiResponse {
  value: number;
  name: string;
}

export interface PriorityApiResponse {
  value: number;
  name: string;
}
