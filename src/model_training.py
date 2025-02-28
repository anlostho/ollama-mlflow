import mlflow
import mlflow.sklearn
from sklearn.linear_model import LinearRegression
from sklearn.ensemble import RandomForestRegressor
from sklearn.metrics import mean_squared_error, r2_score
from data_processing import load_and_preprocess_data
import joblib
import os

def train_and_evaluate(model_name, model, X_train, X_test, y_train, y_test, scaler):
    """Trains, evaluates, and logs a model using MLflow."""
    with mlflow.start_run(run_name=model_name):
        # Log model parameters
        mlflow.log_params(model.get_params())

        # Train the model
        model.fit(X_train, y_train)

        # Make predictions
        y_pred = model.predict(X_test)

        # Evaluate the model
        mse = mean_squared_error(y_test, y_pred)
        r2 = r2_score(y_test, y_pred)

        # Log metrics
        mlflow.log_metric("mse", mse)
        mlflow.log_metric("r2", r2)

        # Log the scaler and the model
        mlflow.sklearn.log_model(scaler, "scaler")
        mlflow.sklearn.log_model(model, "model")
        # save the model and scaler in the folder
        
        model_folder = "models"
        os.makedirs(model_folder, exist_ok=True)
        joblib.dump(model, os.path.join(model_folder,f'{model_name}.joblib'))
        joblib.dump(scaler, os.path.join(model_folder, f'{model_name}_scaler.joblib'))
        print(f"Model {model_name} saved to {model_folder}")
        
        return r2

def main():
    """Main function for training and evaluating multiple models."""
    # Load and preprocess data
    data = load_and_preprocess_data()
    if data is None:
        return
    X_train, X_test, y_train, y_test, scaler = data

    # Define models to train
    models = {
        "LinearRegression": LinearRegression(),
        "RandomForestRegressor": RandomForestRegressor(random_state=42),
        # Add more models here (e.g., SVM, Gradient Boosting)
    }
    best_r2 = -float('inf')
    best_model = None

    for name, model in models.items():
        print(f"Training {name}...")
        r2 = train_and_evaluate(name, model, X_train, X_test, y_train, y_test, scaler)
        if r2 > best_r2:
            best_r2 = r2
            best_model = name

    print(f"Best model: {best_model} with R2: {best_r2}")

if __name__ == "__main__":
    main()
