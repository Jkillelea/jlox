JAVAC = javac
SOURCE_DIR = lox
TOOL_DIR = $(SOURCE_DIR)/tool

all: lox tool

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
