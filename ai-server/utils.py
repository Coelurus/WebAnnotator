import pandas as pd

def extract_features(df: pd.DataFrame, training=True):
    # Ensure sorted by time
    df = df.sort_values(by="timestamp").reset_index(drop=True)

    if training:
        # Identify gesture segments
        segments = []
        current_label = None
        start_idx = 0

        for i in range(len(df)):
            label = df.loc[i, "label"]
            if i == 0:
                current_label = label
                continue

            if label != current_label:
                # Segment ended
                segment = df.iloc[start_idx:i]
                if current_label != "NO_GESTURE":
                    features = compute_segment_features(segment, current_label)
                    segments.append(features)
                start_idx = i
                current_label = label

        # handle last segment
        if current_label != "NO_GESTURE":
            segment = df.iloc[start_idx:]
            features = compute_segment_features(segment, current_label)
            segments.append(features)

        return pd.DataFrame(segments)
    else:
        # For prediction, treat the whole file as one segment (no label)
        df = df.copy()
        return pd.DataFrame([compute_segment_features(df, label=None)])


def compute_segment_features(segment_df: pd.DataFrame, label: str | None):
    """Compute descriptive statistics for a gesture segment"""
    f = {
        "x_mean": segment_df["x"].mean(),
        "y_mean": segment_df["y"].mean(),
        "z_mean": segment_df["z"].mean(),
        "x_std": segment_df["x"].std(),
        "y_std": segment_df["y"].std(),
        "z_std": segment_df["z"].std(),
        "x_min": segment_df["x"].min(),
        "x_max": segment_df["x"].max(),
        "y_min": segment_df["y"].min(),
        "y_max": segment_df["y"].max(),
        "z_min": segment_df["z"].min(),
        "z_max": segment_df["z"].max(),
        "duration": (segment_df["timestamp"].iloc[-1] - segment_df["timestamp"].iloc[0]).total_seconds(),
    }
    if label is not None:
        f["label"] = label
    return f
