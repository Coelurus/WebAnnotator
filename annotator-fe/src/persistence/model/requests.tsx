/**
 * UserRequest interface represents the structure of a request sent to the server
 * when creating or updating a user.
 */
export interface UserRequest {
  /**
   * First name of the user.
   */
  firstName?: string;
  /**
   * Last name of the user.
   */
  lastName?: string;
  /**
   * Username of the user.
   */
  username?: string;
  /**
   * Role of the user in the system.
   */
  role?: string;
  /**
   * ID of the team the user belongs to. If null, the user is not part of any team.
   */
  teamId?: number | null;
}

/**
 * TeamRequest interface represents the structure of a request sent to the server
 * when creating or updating a team.
 */
export interface TeamRequest {
  /**
   * Name of the team.
   */
  name?: string;
  /**
   * ID of the leader of the team. If null, the team has no leader.
   */
  leaderId?: number;
}

/**
 * ProjectRequest interface represents the structure of a request sent to the server
 * when creating or updating a project.
 */
export interface ProjectRequest {
  /**
   * Name of the project.
   */
  projectName?: string;
  /**
   * Deadline for the project in format YYYY-MM-DD.
   */
  deadline?: string;
  /**
   * Progress of the project represanted by its name.
   */
  progress?: string;
  /**
   * Priority of the project represented by its name.
   */
  priority?: string;
  /**
   * ID of the team associated with the project. If null, the project is not assigned to any team.
   */
  teamId?: number | null;
  /**
   * File containing the log of the project and frames.
   */
  file?: File;
}

/**
 * LabelRequest interface represents the structure of a request sent to the server
 * when creating or updating a label.
 */
export interface LabelRequest {
  /**
   * Name of the label.
   */
  labelName?: string;
  /**
   * Color of the label in hexadecimal format.
   */
  color?: string;
}
