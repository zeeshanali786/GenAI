
## Run the application

### Part 1: Application Dependencies

1. Install application dependencies
    - From the project's root directory, run: `mvn clean install -DskipTests`

### Part 2: Run Player Service (without LLM)

1. Start the Player service

   ```shell
   mvn spring-boot:run
   ```

2. Verify the Player service is running
      1. Open your browser and visit `http://localhost:8080/v1/players`
      2. If the application is running successfully, you will see player data appear in the browser

### Part 3: Start LLM Docker Container

Player service integrates with Ollama 🦙, which allows us to run LLMs locally. This app runs [tinyllama](https://ollama.com/library/tinyllama) model.

- [Ollama API documentation](https://github.com/ollama/ollama/blob/main/docs/api.md)
- [Ollama4J SDK](https://ollama4j.github.io/ollama4j/intro)

1. Pull and run Ollama docker image and download `tinyllama` model
   - Pull Ollama docker image

    ```shell
    docker pull ollama/ollama
    ```

2. Run Ollama docker image on port 11434 as a background process

    ```shell
    docker run -d -v ollama:/root/.ollama -p 11434:11434 --name ollama ollama/ollama
    ```

3. Download and run `tinyllama` model

    ```shell
    docker exec -it ollama ollama run tinyllama
    ```

4. Test Ollama API server

    ```curl
    curl -v --location 'http://localhost:11434/api/generate' --header 'Content-Type: application/json' --data '{"model": "tinyllama","prompt": "why is the sky blue?", "stream": false}'
    ```
### Part 4: Verify Player Service and LLM Integration

1. Ensure Player Service is running from previous instructions. If not:

    ```shell
    mvn spring-boot:run
    ```

2. Open your browser and visit `http://localhost:8080/v1/chat/list-models`
   - If the application is running successfully, you will see a json response that include information about tinyllama
