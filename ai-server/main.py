from fastapi import FastAPI, UploadFile, File, Body
from fastapi.responses import JSONResponse
import pandas as pd
from io import StringIO
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
import joblib
from pathlib import Path
from utils import extract_features
import logging

# Configure logging
logging.basicConfig(level=logging.INFO, format='{"timestamp": "%(asctime)s", "level": "%(levelname)s", "message": "%(message)s"}')
logger = logging.getLogger(__name__)

app = FastAPI(title="Gesture AI Server")

MODELS_DIR = Path("models")
MODELS_DIR.mkdir(exist_ok=True)

@app.post("/api/ai/{project_id}")
async def train_model(project_id: int, csv_data: str = Body(...)):
    logger.info("Training started", extra={"project_id": project_id, "csv_length": len(csv_data)})

    try:
        data_dir = Path("data")
        data_dir.mkdir(exist_ok=True)
        csv_path = data_dir / f"{project_id}.csv"

        # Save CSV for future use
        with open(csv_path, "w") as f:
            f.write(csv_data)

        # Collect all stored data
        all_dfs = []
        for file in data_dir.glob("*.csv"):
            df = pd.read_csv(file)
            if {"timestamp", "x", "y", "z", "label"}.issubset(df.columns):
                df["timestamp"] = pd.to_datetime(df["timestamp"])
                all_dfs.append(df)
        if not all_dfs:
            return JSONResponse(status_code=400, content={"error": "No valid data found for training."})

        full_df = pd.concat(all_dfs, ignore_index=True)
        logger.info("All data combined", extra={"projects": len(all_dfs), "total_rows": len(full_df)})

        # Extract features
        feature_df = extract_features(full_df)
        X = feature_df.drop("label", axis=1)
        y = feature_df["label"]

        # Split and scale
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
        scaler = StandardScaler()
        X_train_scaled = scaler.fit_transform(X_train)
        X_test_scaled = scaler.transform(X_test)

        # Train (global) model
        clf = RandomForestClassifier(n_estimators=100, random_state=42)
        clf.fit(X_train_scaled, y_train)
        accuracy = clf.score(X_test_scaled, y_test)

        # Save global model
        joblib.dump((clf, scaler), MODELS_DIR / "global_model.pkl")

        logger.info("Global training completed", extra={
            "projects": len(all_dfs),
            "accuracy": accuracy,
            "samples": len(feature_df)
        })
        return {"status": "global model updated", "accuracy": accuracy, "projects": len(all_dfs)}

    except Exception as e:
        logger.error("Training failed", extra={"project_id": project_id, "error": str(e)})
        return JSONResponse(status_code=500, content={"error": str(e)})



@app.post("/api/ai/{project_id}/predict")
async def predict_gesture_segments(project_id: int, csv_data: str = Body(...)):
    logger.info("Prediction started", extra={"project_id": project_id, "csv_length": len(csv_data)})
    model_path = MODELS_DIR / "global_model.pkl"
    if not model_path.exists():
        logger.warning("Model not found", extra={"project_id": project_id})
        return JSONResponse(status_code=404, content={"error": "Model not found. Train it first."})

    try:
        clf, scaler = joblib.load(model_path)
        df = pd.read_csv(StringIO(csv_data))

        if not {"timestamp", "x", "y", "z"}.issubset(df.columns):
            logger.warning("Invalid CSV columns for prediction", extra={"project_id": project_id, "columns": list(df.columns)})
            return JSONResponse(status_code=400, content={
                "error": "CSV must contain columns: timestamp, x, y, z"
            })

        # Convert timestamp to datetime
        df["timestamp"] = pd.to_datetime(df["timestamp"])

        # Sort & reset index
        df = df.sort_values("timestamp").reset_index(drop=True)

        window_size = 20   # number of frames per window
        step_size = 10     # how far to slide (half-overlap)
        predictions = []

        for start in range(0, len(df) - window_size, step_size):
            end = start + window_size
            segment = df.iloc[start:end]
            features = extract_features(segment, training=False)
            X_scaled = scaler.transform(features)
            pred = clf.predict(X_scaled)[0]
            predictions.append({"start": start, "end": end, "gesture": pred})

        # Merge consecutive windows with the same label
        merged_segments = []
        current = None
        for p in predictions:
            if current is None:
                current = p
            elif p["gesture"] == current["gesture"]:
                current["end"] = p["end"]
            else:
                merged_segments.append(current)
                current = p
        if current:
            merged_segments.append(current)

        # Filter out "NO_GESTURE" if model might predict it
        merged_segments = [seg for seg in merged_segments if seg["gesture"] != "NO_GESTURE"]

        logger.info("Prediction completed", extra={"project_id": project_id, "segments": len(merged_segments)})
        return {"segments": merged_segments}

    except Exception as e:
        logger.error("Prediction failed", extra={"project_id": project_id, "error": str(e)})
        return JSONResponse(status_code=500, content={"error": str(e)})
