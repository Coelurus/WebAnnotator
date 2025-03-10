export interface ProjectApiResponse<TeamResponse> {
  id: number;
  project_name: string;
  log_file_name: string;
  dead_line: string;
  priority: number;
  team: TeamResponse;
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
  first_name: string;
  last_name: string;
}

export interface UserApiResponse {
  id: number;
  first_name: string;
  last_name: string;
  username: string;
  role: string;
  team: ShortTeamApiResponse | null;
}

export interface LabelApiResponse {
  id: number,
  labelName: string,
  color: string
}

export interface FrameCountApiResponse {
  count: number,
}

export interface AnnotationApiResponse {
  projectId: number,
  frameId: number,
  labelId: number,
}