/**
 * LongTeamApiResponse interface represents the structure of a team response including
 * information about the team leader.
 */
export interface LongTeamApiResponse {
  /**
   * Unique identifier for the team.
   */
  id: number;
  /**
   * Name of the team.
   */
  name: string;
  /**
   * Leader of the team, represented by a ShortUserApiResponse object or null if no leader is assigned.
   */
  leader: ShortUserApiResponse | null;
}

/**
 * ShortTeamApiResponse interface represents the structure of a team response without
 * information about the team leader.
 */
export interface ShortTeamApiResponse {
  /**
   * Unique identifier for the team.
   */
  id: number;
  /**
   * Name of the team.
   */
  name: string;
}

/**
 *  ProjectApiResponse interface represents the structure of a project response
 */
export interface ProjectApiResponse<ShortTeamApiResponse> {
  /**
   * Unique identifier for the project.
   */
  id: number;
  /**
   * Name of the project.
   */
  projectName: string;
  /**
   * Name of the log file associated with the project.
   */
  logFileName: string;
  /**
   * Deadline for the project.
   */
  deadline: string;
  /**
   * Priority of the project.
   */
  priority: string;
  /**
   * Progress of the project.
   */
  progress: string;
  /**
   * Team associated with the project, represented by a ShortTeamApiResponse object or null if no team is assigned.
   */
  team: ShortTeamApiResponse | null;
}

/**
 * ShortUserApiResponse interface represents a simplified user response
 * without the team user is part of.
 */
export interface ShortUserApiResponse {
  /**
   * Unique identifier for the user.
   */
  id: number;
  /**
   * First name of the user.
   */
  firstName: string;
  /**
   * Last name of the user.
   */
  lastName: string;
}

/**
 * LongUserApiResponse interface represents a detailed user response
 * including the team the user is part of.
 */
export interface LongUserApiResponse {
  /**
   * Unique identifier for the user.
   */
  id: number;
  /**
   * First name of the user.
   */
  firstName: string;
  /**
   * Last name of the user.
   */
  lastName: string;
  /**
   * Username of the user.
   */
  username: string;
  /**
   * Role of the user in the system.
   */
  role: string;
  /**
   * Team the user is part of, represented by a ShortTeamApiResponse object or null if user is not in any team.
   */
  team: ShortTeamApiResponse | null;
}

/**
 * LabelApiResponse interface represents the structure of a label response
 */
export interface LabelApiResponse {
  /**
   * Unique identifier for the label.
   */
  id: number;
  /**
   * Name of the label.
   */
  labelName: string;
  /**
   * Color associated with the label.
   */
  color: string;
}

/**
 * FrameCountApiResponse interface represents the structure of a frame count response
 */
export interface FrameCountApiResponse {
  /**
   * Count of frames in the project.
   */
  count: number;
}

/**
 * AnnotationApiResponse interface represents the structure of an annotation response
 */
export interface AnnotationApiResponse {
  /**
   * Unique identifier of the project where the annotation belongs.
   */
  projectId: number;
  /**
   * Unique identifier of the frame from the project where the annotation is made.
   */
  frameId: number;
  /**
   * Unique identifier of the label associated with the annotation.
   */
  labelId: number;
}

/**
 * ProgressApiResponse interface represents the structure of a projects progress response
 */
export interface ProgressApiResponse {
  /**
   * Numeric value representing the progress of the project.
   */
  value: number;
  /**
   * Name of the progress - string representation of the progress status.
   */
  name: string;
}

/**
 * PriorityApiResponse interface represents the structure of a projects priority response
 */
export interface PriorityApiResponse {
  /**
   * Numeric value representing the priority of the project - Greater values indicate higher priority.
   */
  value: number;
  /**
   * Name of the priority - string representation of the priority level.
   */
  name: string;
}
