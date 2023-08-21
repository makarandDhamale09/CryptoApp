import React from "react";
import Navbar from "./Navbar";
import { useParams } from "react-router";
import CoinChartContainer from "./CoinChartContainer";

function CoinDetailsPage() {
  const { coinSymbol } = useParams();
  return (
    <>
      <Navbar />
      <CoinChartContainer slug={coinSymbol} />
    </>
  );
}

export default CoinDetailsPage;
