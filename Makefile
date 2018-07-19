JAVAC = javac
SOURCE_DIR = com/craftinginterpreters/lox
TOOL_DIR = com/craftinginterpreters/tool

all: lox tool | $(BUILD_TREE)

# .PHONY: lox
lox: $(SOURCE_DIR)/*.class

$(SOURCE_DIR)/%.class: $(SOURCE_DIR)/%.java
	$(JAVAC) $^

# .PHONY: tool
tool: $(TOOL_DIR)/*.class

$(TOOL_DIR)/%.class: $(TOOL_DIR)/%.java
	$(JAVAC) $^

.PHONY: clean
clean:
	rm -f $(SOURCE_DIR)/*.class
	rm -f $(TOOL_DIR)/*.class
