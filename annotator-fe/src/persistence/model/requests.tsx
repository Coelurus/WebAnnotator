export interface UserRequest {
  firstName?: string;
  lastName?: string;
  username?: string;
  role?: string;
  teamId?: number | null;
}

export interface TeamRequest {
  name?: string;
  leaderId?: number;
}

export interface ProjectRequest {
  projectName?: string;
  deadline?: string;
  progress?: string;
  priority?: string;
  teamId?: number | null;
  file?: File;
}

export interface LabelRequest {
  labelName?: string;
  color?: string;
}
