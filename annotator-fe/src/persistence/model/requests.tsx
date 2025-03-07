export interface UserRequest {
    firstName?: string,
    lastName?: string,
    username?: string,
    role?: string,
    teamId?: number | null
}

export interface ProjectRequest {
    projectName?: string,
    deadline?: string;
    priority?: string,
    teamId?: number | null,
    file?: File,
}