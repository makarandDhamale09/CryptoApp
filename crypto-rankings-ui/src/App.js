import "./App.css";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import MainPage from "./components/MainPage";
import CoinDetailsPage from "./components/CoinDetailsPage";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/coin/:coinSymbol" element={<CoinDetailsPage />} />
      </Routes>
    </Router>
  );
}

export default App;
