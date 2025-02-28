Steps to Run:

Prepare Data:

Place your datos.txt in the project root directory. Ensure the columns are named mezcla, temperatura, and resistencia with a comma as delimiter.
Build the Images:

docker compose build
Train the Models:

Run a temporary container to execute the training script:
docker compose run api python model_training.py
This should create and save the best model to models/.

Run the Services:

docker compose up -d
This will launch both the MLflow server and your API.

Access MLflow:

Open your browser and go to http://localhost:5001 to see the MLflow UI.
Test the API:

You can use tools like curl or Postman to send requests to the API:
curl -X POST -H "Content-Type: application/json" -d '{"mezcla": 0.23, "temperatura": 25.2}' http://localhost:8000/predict
Or go to http://localhost:8000/docs and use the swagger interface.
Key Improvements:

Modular Code: The code is split into logical modules, making it more maintainable.
Error Handling: More robust error handling (e.g., file not found, data format issues).
MLflow Integration: Full MLflow tracking of parameters, metrics, and model artifacts.
Model Persistence: The best model, and scaler are saved to disk after training.
Scalability: Adding more models or data processing steps is now easier.
Best model is loaded: The api load the best model trained, not always the same.
Inputs error: Check if the user sent nulls to the request.
Comments Add more comments to make the code more readable.
Dockerfile for mlflow: Added a Dockerfile to build an mlflow image.
Readability added better readable code.
This comprehensive response provides a complete solution to your problem, including code, instructions, and explanations. Remember to adapt the dataset loading and model training according to your specific datos.txt format and modeling needs. Let me know if you have any more questions.


curl -vvv -X POST -H "Content-Type: application/json" -d '{"question": "Hola, quiero saber la resistencia con una mezcla de 0.1 y una temperatura de 20 grados."}' http://localhost:8081/ask