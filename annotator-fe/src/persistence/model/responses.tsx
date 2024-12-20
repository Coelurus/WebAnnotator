export interface UserResponse {
    id: number;
    firstName: string;
    lastName: string;
    username: string;
    team: ShortTeamResponse;
}

export interface ShortUserResponse {
    id: number;
    firstName: string;
    lastName: string;
}

export interface TeamResponse {
    id: number;
    name: string;
    leader: ShortUserResponse;
}

export interface ShortTeamResponse {
    id: number;
    name: string;
}

export interface ProjectResponse {
    id: number;
    projectName: string;
    logFileName: string;
    deadline: string;
    priority: number;
    team: ShortTeamResponse;
}

export interface PriorityResponse {
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
