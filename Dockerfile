# Use an official Python runtime as a parent image
FROM python:3.9-slim-buster

# Set the working directory to /app
WORKDIR /app

# Copy the requirements file into the container at /app
COPY src/requirements.txt ./

# Install any needed packages specified in requirements.txt
RUN pip install --no-cache-dir -r requirements.txt

# Copy the current directory contents into the container at /app
COPY src/ .
COPY datos.txt .

# Expose port 8000
EXPOSE 8000

# Define environment variable
ENV NAME World

# Run model_serving.py when the container launches
CMD ["uvicorn", "model_serving:app", "--host", "0.0.0.0", "--port", "8000"]
