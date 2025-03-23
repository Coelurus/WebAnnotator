.PHONY: all install runtest clean

SRC_DIR = src/utils
TEST_DIR = test/utils
TEST_RESULTS = test_results.txt
VENV_DIR = venv
PYTHON = $(VENV_DIR)/bin/python
SRC_FILES = $(wildcard $(SRC_DIR)/*.py)
TEST_FILES = $(wildcard $(TEST_DIR)/*.py)

all: $(TEST_FILES) install
	@echo "Running all tests..."
	@for test in $(TEST_FILES); do \
		if $(PYTHON) $$test; then \
			echo "[PASSED]: $$test" | tee -a $(TEST_RESULTS); \
		else \
			echo "[FAILED]: $$test" | tee -a $(TEST_RESULTS); \
		fi; \
	done

updated: $(SRC_FILES)
	@echo "Reruning tests for modified files..."
	@for src in $(SRC_FILES); do \
		test="$(TEST_DIR)/test_$$(basename $$src)"; \
		if [ -f $$test ] && [ $$src -nt $(TEST_RESULTS) ]; then \
			echo "Running $$test"; \
			if $(PYTHON) $$test; then \
				echo "[PASSED]: $$test" | tee -a $(TEST_RESULTS); \
			else \
				echo "[FAILED]: $$test" | tee -a $(TEST_RESULTS); \
			fi; \
		fi; \
	done

install: $(VENV_DIR)/bin/activate
	$(PYTHON) -m pip install opencv-python
	$(PYTHON) -m pip install PyAutoGUI

$(VENV_DIR)/bin/activate:
	apt install python3.12-venv
	python3 -m venv $(VENV_DIR)
	$(PYTHON) -m pip install --upgrade pip

runtest:
	@echo "Running specified test..."
	@read -p "Enter the test file name: " testfile; \
	if [ -f $(TEST_DIR)/$$testfile ]; then \
		$(PYTHON) $(TEST_DIR)/$$testfile; \
	else \
		echo "Test file not found!"; \
	fi

clean:
	@echo "Cleaning up..."
	@rm -f $(TEST_RESULTS)
	@rm -rf $(VENV_DIR)
	@echo "Done!"

# Automatization of testing python script. 
# The all target runs all the tests in the test directory. 
# The updated target runs the tests for source files that have been modified. 
# The install target installs the opencv-python package. 
# The runtest target runs a specified test file. 
# The clean target removes the test results and the virtual environment