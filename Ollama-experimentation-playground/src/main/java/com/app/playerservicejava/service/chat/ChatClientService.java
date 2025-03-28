package com.app.playerservicejava.service.chat;

import com.app.playerservicejava.model.Player;
import com.app.playerservicejava.model.Players;
import com.app.playerservicejava.service.PlayerService;
import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.exceptions.OllamaBaseException;
import io.github.ollama4j.exceptions.ToolInvocationException;
import io.github.ollama4j.models.Model;
import io.github.ollama4j.models.OllamaResult;
import io.github.ollama4j.models.chat.OllamaChatMessageRole;
import io.github.ollama4j.models.chat.OllamaChatRequestBuilder;
import io.github.ollama4j.models.chat.OllamaChatResult;
import io.github.ollama4j.tools.OllamaToolsResult;
import io.github.ollama4j.tools.Tools;
import io.github.ollama4j.types.OllamaModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.github.ollama4j.utils.OptionsBuilder;
import io.github.ollama4j.utils.PromptBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ChatClientService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatClientService.class);

    @Autowired
    private OllamaAPI ollamaAPI;
    @Autowired
    private PlayerService playerService;

    public List<Model> listModels() throws OllamaBaseException, IOException, URISyntaxException, InterruptedException {
        List<Model> models = ollamaAPI.listModels();
        return models;
    }

    public String chat() throws OllamaBaseException, IOException, InterruptedException {
        String model = OllamaModelType.TINYLLAMA;

        // https://ollama4j.github.io/ollama4j/intro
       // PromptBuilder promptBuilder =
         //       new PromptBuilder()
         //               .addLine("Recite a haiku about recursion.");




        OllamaChatRequestBuilder builder = OllamaChatRequestBuilder.getInstance(OllamaModelType.TINYLLAMA);

        // create first user question
        var requestModel = builder.withMessage(OllamaChatMessageRole.USER, "Below is a dataset of players: - Name: David, Age: 1981. What is the age of David?")
                .build();
        // start conversation with model
        OllamaChatResult chatResult = ollamaAPI.chat(requestModel);

        System.out.println("First answer: " + chatResult);

        PromptBuilder promptBuilder = new PromptBuilder()
                .addLine("<|system|>")
                .addLine("You are an intelligent assistant.")
                .addLine("<|user|>")
                .addLine("Below is a dataset of players:")
                .addLine("- Name: Alice, Age: 1990")
                .addLine("- Name: Bob, Age: 1985")
                .addLine("- Name: David, Age: 1981")
                .addLine("\nTask: What is the age of the player named 'David'? Calculate the age as 2025 - Birth Year.")
                .addLine("<|assistant|>");
         boolean raw = false;
          OllamaResult response = ollamaAPI.generate(model, promptBuilder.build(), raw, new OptionsBuilder().build());
          return response.getResponse();
    }

    public String advChat() throws OllamaBaseException, IOException, InterruptedException {
        String model = OllamaModelType.TINYLLAMA;

        Players players = playerService.getPlayers();


        PromptBuilder promptBuilder = new PromptBuilder()
                .addLine("Below is a dataset of players containing names and ages:");

        boolean davidIncluded = false;
        for (Player player : players.getPlayers()) {
            String name = (player.getFirstName() != null) ? player.getFirstName() : "Unknown";
            String age = (player.getBirthYear() != null) ? player.getBirthYear() : "Unknown";

            // Add David explicitly if not yet included
            if (name.equalsIgnoreCase("David")) {
                davidIncluded = true;
            }

            promptBuilder.addLine(
                    String.format("- Name: %s, Age: %s", name, age)
            );
        }

        // Ensure David is included if not found in the original dataset
        if (!davidIncluded) {
            promptBuilder.addLine("- Name: David, Age: 1981");
        }

        // Add the question/query at the end of the prompt
        promptBuilder.addLine("\nQuestion: Based on the dataset above, what is the age of the player named David?");

        boolean raw = false;
        OllamaResult response = ollamaAPI.generate(model, promptBuilder.build(), raw, new OptionsBuilder().build());
        return response.getResponse();
    }

    public String newChat() throws OllamaBaseException, IOException, InterruptedException, ToolInvocationException {
        String model = OllamaModelType.TINYLLAMA;
        Tools.ToolSpecification fuelPriceToolSpecification = Tools.ToolSpecification.builder()
                .functionName("current-fuel-price")
                .functionDescription("Get current fuel price")
                .properties(
                        new Tools.PropsBuilder()
                               // .withProperty("location", Tools.PromptFuncDefinition.Property.builder().type("string").description("The city, e.g. New Delhi, India").required(true).build())
                                .withProperty("fuelType", Tools.PromptFuncDefinition.Property.builder().type("string").description("The fuel type.").enumValues(Arrays.asList("petrol", "diesel")).required(true).build())
                                .build()
                )
                .toolDefinition(this::getCurrentFuelPrice)
                .build();

        ollamaAPI.registerTool(fuelPriceToolSpecification);

        String prompt1 = new Tools.PromptBuilder()
                .withToolSpecification(fuelPriceToolSpecification)
                .withPrompt("What is the petrol price?")
                .build();

        OllamaToolsResult toolsResult = ollamaAPI.generateWithTools(model, prompt1, new OptionsBuilder().build());
        for (OllamaToolsResult.ToolResult r : toolsResult.getToolResults()) {
            System.out.printf("[Result of executing tool '%s']: %s%n", r.getFunctionName(), r.getResult().toString());
        }

        return  toolsResult.getToolResults().toString();
    }

    public static void ask(OllamaAPI ollamaAPI, String model, String prompt) throws OllamaBaseException, IOException, InterruptedException, ToolInvocationException, ToolInvocationException {
        OllamaToolsResult toolsResult = ollamaAPI.generateWithTools(model, prompt, new OptionsBuilder().build());
        for (OllamaToolsResult.ToolResult r : toolsResult.getToolResults()) {
            System.out.printf("[Result of executing tool '%s']: %s%n", r.getFunctionName(), r.getResult().toString());
        }
    }
    public String getCurrentFuelPrice(Map<String, Object> arguments) {
        // Get details from fuel price API
        //String location = arguments.get("location").toString();
        String fuelType = arguments.get("fuelType").toString();
        return "Current price of " + fuelType + " is 100";
    }

}
