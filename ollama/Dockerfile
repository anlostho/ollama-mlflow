FROM ollama/ollama:latest

RUN apt-get update && apt-get install -y curl


# Copy the startup script into the container
COPY startup.sh /usr/local/bin/startup.sh

# Make the script executable
RUN chmod +x /usr/local/bin/startup.sh

# Set the startup script as the entry point
ENTRYPOINT ["/usr/local/bin/startup.sh"]