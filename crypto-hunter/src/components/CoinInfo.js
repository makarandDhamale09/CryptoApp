import React, { useEffect, useState } from "react";
import { CryptoState } from "../CryptoContext";
import axios from "axios";
import { HistoricalChart } from "../config/api";
import {
  CircularProgress,
  ThemeProvider,
  createTheme,
  makeStyles,
} from "@material-ui/core";
import { Line } from "react-chartjs-2";
import { Chart } from "chart.js/auto";

const CoinInfo = ({ coin }) => {
  const [historicData, setHistoricData] = useState();
  const [days, setDays] = useState(1);

  const { currency } = CryptoState();

  const fetchHistoricData = async () => {
    const { data } = await axios.get(HistoricalChart(coin?.id, days, currency));

    setHistoricData(data.prices);
  };

  useEffect(() => {
    fetchHistoricData();
  }, [days, currency]);

  const darkTheme = createTheme({
    palette: {
      primary: {
        main: "#ffff",
      },
      type: "dark",
    },
  });

  const useStyles = makeStyles((theme) => ({
    container: {
      width: "70%",
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      marginTop: 25,
      padding: 40,
      [theme.breakpoints.down("md")]: {
        width: "100%",
        marginTop: 10,
        padding: 20,
      },
    },
  }));

  const classes = useStyles();

  return (
    <ThemeProvider theme={darkTheme}>
      <div className={classes.container}>
        {!historicData ? (
          <CircularProgress
            style={{ color: "gold" }}
            size={200}
            thickness={1}
          />
        ) : (
          <>
            <Line
              data={{
                labels: historicData.map((coin) => {
                  let date = new Date(coin[0]);
                  let time =
                    date.getHours() > 12
                      ? `${date.getHours() - 12}:${date.getMinutes()} PM`
                      : `${date.getHours()}:${date.getMinutes()} AM`;
                  return days === 1 ? time : date.toLocaleDateString();
                }),
                datasets: [
                  {
                    data: historicData.map((coin) => coin[1]),
                    label: `Price (Past ${days} Days) in ${currency}`,
                    borderColor: "#EEBC1D",
                  },
                ],
              }}
            />
          </>
        )}
      </div>
    </ThemeProvider>
  );
};

export default CoinInfo;
