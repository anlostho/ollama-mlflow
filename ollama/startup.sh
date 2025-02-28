#!/bin/bash

ollama serve &

# Wait for Ollama server to be ready
max_retries=30
retry_delay=2

for i in $(seq 1 $max_retries); do
  echo "Checking if Ollama server is ready (attempt $i)..."
  if curl -s http://localhost:11434/api/tags > /dev/null; then
    echo "Ollama server is ready."
    break
  else
    echo "Ollama server not ready yet. Retrying in $retry_delay seconds..."
    sleep $retry_delay
  fi
  if [[ $i -eq $max_retries ]]; then
    echo "Error: Ollama server did not become ready after $max_retries attempts."
    exit 1
  fi
done

# Check if the model is already downloaded
if ! ollama list | grep -q "orca-mini"; then
  echo "Downloading orca-mini model..."
  ollama pull orca-mini
else
  echo "orca-mini model already exists."
fi

wait