export interface ShortUser {
  id: number;
  firstName: string;
  lastName: string;
}

export interface LongUser {
  id: number;
  firstName: string;
  lastName: string;
  username: string;
  role: string;
  team: ShortTeam | null;
}

export interface ShortTeam {
  id: number;
  name: string;
}

export interface LongTeam {
  id: number;
  name: string;
  leader: ShortUser | null;
}

export interface Project {
  id: number;
  projectName: string;
  logFileName: string;
  deadline: string;
  priority: number;
  team: ShortTeam | null;
}

export interface Priority {
  name: string;
}

export interface Annotation {
  frameId: number;
  labelId: number;
}

export interface Label {
  id: number;
  label: string;
  color: string;
}

export interface PredictionTriple {
  projectId: number;
  frameId: number;
  label: string;
}
