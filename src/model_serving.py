from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import joblib
import os
from sklearn.preprocessing import StandardScaler
import numpy as np

app = FastAPI()
# load the best model
models_folder = 'models'
# Get a list of all files in the directory
all_files = os.listdir(models_folder)

# Filter for .joblib files
joblib_files = [f for f in all_files if f.endswith('.joblib') and not f.endswith("_scaler.joblib")]
if len(joblib_files) == 0:
    print("No .joblib files found in the directory.")
    best_model = None
else:
    # Find the first file
    best_model = joblib_files[0]
    print(f"Best Model Name: {best_model}")

# Get the name of the best scaler
if best_model is not None:
    best_scaler = f"{best_model.split('.')[0]}_scaler.joblib"
else:
    best_scaler = None

try:
    if best_model is not None:
        model = joblib.load(os.path.join(models_folder,best_model))
        scaler = joblib.load(os.path.join(models_folder,best_scaler))
    else:
        print("No model is loaded")
except FileNotFoundError as e:
    print(f"Error: {e}")
    model = None
    scaler = None

class PredictionInput(BaseModel):
    mezcla: float
    temperatura: float


@app.post("/predict")
async def predict(input_data: PredictionInput):
    """Predicts resistance based on mezcla and temperatura."""
    if model is None or scaler is None:
      raise HTTPException(status_code=500, detail="Model not trained or not loaded")

    try:
      # Ensure the inputs are not null
      if input_data.mezcla is None or input_data.temperatura is None:
          raise ValueError("Mezcla and Temperatura cannot be null")
      # Prepare the input data
      input_array = np.array([[input_data.mezcla, input_data.temperatura]])
      # Scale the input data
      scaled_input = scaler.transform(input_array)
      prediction = model.predict(scaled_input)[0]
      return {"resistencia": prediction}
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
      raise HTTPException(status_code=500, detail=str(e))
