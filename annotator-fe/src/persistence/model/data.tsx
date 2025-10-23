/**
 * ShortUser interface represents a user with shortened details.
 */
export interface ShortUser {
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
 * LongUser interface represents a user with detailed information,
 * including their team and role.
 */
export interface LongUser {
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
   * Team the user is part of, represented by a ShortTeam object or null if user is not in any team.
   */
  team: ShortTeam | null;
}

/**
 * ShortTeamApiResponse interface represents a simplified team response
 * without the leader user.
 */
export interface ShortTeam {
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
 * LongTeamApiResponse interface represents a detailed team response
 * including the leader user.
 */
export interface LongTeam {
  /**
   * Unique identifier for the team.
   */
  id: number;
  /**
   * Name of the team.
   */
  name: string;
  /**
   * Leader of the team, represented by a ShortUser object or null if team has no leader.
   */
  leader: ShortUser | null;
}

/**
 * Project interface represents a project with all its details.
 */
export interface Project {
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
   * Progress of the project, represented as a string.
   */
  progress: string;
  /**
   * Team associated with the project, represented by a ShortTeam object or null if no team is assigned.
   */
  team: ShortTeam | null;
}

/**
 * Priority interface represents the priority of finishing a project.
 */
export interface Priority {
  /**
   * Numeric value representing the priority level - Greater values indicate higher priority
   */
  value: number;
  /**
   * Name of the priority level.
   */
  name: string;
}

/**
 * Annotation interface represents an annotation made on a frame in a project.
 */
export interface Annotation {
  /**
   * ID of the frame in the project where the annotation is made.
   */
  frameId: number;
  /**
   * ID of label associated with the frame.
   */
  labelId: number;
}

/**
 * Label interface represents a label used for annotating frames in a project.
 */
export interface Label {
  /**
   * Unique identifier for the label.
   */
  id: number;
  /**
   * Name of the label.
   */
  label: string;
  /**
   * Color associated with the label, used for visual representation.
   */
  color: string;
}

/**
 * PredictionTriple interface represents a prediction made by the model of which label is assigned to which frame in a project.
 */
export interface PredictionTriple {
  /**
   * Unique identifier for the project.
   */
  projectId: number;
  /**
   * Unique identifier of the frame from the project where the prediction is made.
   */
  frameId: number;
  /**
   * Name of the label assigned to the frame by the model.
   */
  label: string;
}

/**
 * Progress interface represents the progress of a project.
 */
export interface Progress {
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
 * AIModelUpdateResponse interface represents the response from AI model training.
 */
export interface AIModelUpdateResponse {
  /**
   * Status message from the AI service.
   */
  status: string;
  /**
   * Accuracy of the trained model.
   */
  accuracy: number;
  /**
   * Number of projects used in training.
   */
  projects: number;
}
