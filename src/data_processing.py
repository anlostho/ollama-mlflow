import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler

def load_and_preprocess_data(filepath="datos.txt"):
    """Loads, preprocesses, and splits the data."""
    try:
        # Load the data, stripping whitespace from column names
        df = pd.read_csv(filepath, sep=',', skipinitialspace=True)
        # Remove white space from the headers
        df.columns = df.columns.str.strip()

    except FileNotFoundError:
        print(f"File not found: {filepath}. Ensure the file exists and the path is correct.")
        return None
    except pd.errors.EmptyDataError:
        print(f"The file {filepath} is empty.")
        return None
    except pd.errors.ParserError:
        print(f"The file {filepath} can't be parsed, ensure it's a correctly formated csv.")
        return None
    
    # Check if the needed column exist
    required_columns = ["mezcla","temperatura","resistencia"]
    if not all(col in df.columns for col in required_columns):
        missing_columns = [col for col in required_columns if col not in df.columns]
        print(f"The file is missing the following columns: {', '.join(missing_columns)}. Ensure that the columns are in the file.")
        return None
    
    # Handle missing values (example: imputation with mean)
    df.fillna(df.mean(), inplace=True)

    # Feature scaling
    scaler = StandardScaler()
    X = scaler.fit_transform(df[["mezcla", "temperatura"]])
    y = df["resistencia"]

    # Split into train and test sets
    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=0.2, random_state=42
    )

    return X_train, X_test, y_train, y_test, scaler
