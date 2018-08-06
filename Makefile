JAVAC = javac
SOURCE_DIR = com/craftinginterpreters/lox
TOOL_DIR = com/craftinginterpreters/tool

all:
	$(MAKE) tool
	$(MAKE) file_generation
	$(MAKE) lox

.PHONY: lox
lox: $(SOURCE_DIR)/*.class

$(SOURCE_DIR)/%.class: $(SOURCE_DIR)/%.java
	$(JAVAC) $^

.PHONY: tool
tool: $(TOOL_DIR)/*.class

$(TOOL_DIR)/%.class: $(TOOL_DIR)/%.java
	$(JAVAC) $^

.PHONY: expr_generation
file_generation: tool
	java com.craftinginterpreters.tool.GenerateAst $(SOURCE_DIR)

.PHONY: clean
clean:
	rm -f $(SOURCE_DIR)/*.class
	rm -f $(TOOL_DIR)/*.class
