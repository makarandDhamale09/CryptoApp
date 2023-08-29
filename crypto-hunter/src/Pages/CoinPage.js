import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { CryptoState } from "./../CryptoContext";
import axios from "axios";
import { SingleCoin } from "../config/api";
import { LinearProgress, Typography, makeStyles } from "@material-ui/core";
import CoinInfo from "../components/CoinInfo";
import { numberWithCommas } from "../components/Banner/Carousel";

const CoinPage = () => {
  const { id } = useParams();
  const [coin, setCoin] = useState();

  const { currency, symbol } = CryptoState();

  const fetchCoin = async () => {
    const { data } = await axios.get(SingleCoin(id));
    setCoin(data);
  };

  console.log(coin);

  useEffect(() => {
    fetchCoin();
  }, []);

  const useStyles = makeStyles((theme) => ({
    container: {
      display: "Flex",
      [theme.breakpoints.down("md")]: {
        flexDirection: "column",
        alignItems: "center",
      },
    },
    sidebar: {
      width: "32%",
      [theme.breakpoints.down("md")]: {
        width: "100%",
      },
      display: "flex",
      flexDirection: "column",
      alignItems: "center",
      marginTop: 25,
      borderRight: "2px solid grey",
    },
    heading: {
      fontWeight: "bold",
      marginBottom: 20,
      fontFamily: "Montserrat",
    },
    description: {
      width: "100%",
      fontFamily: "Montserrat",
      padding: 25,
      paddingBottom: 15,
      paddingTop: 0,
      textAlign: "justify",
    },
    marketData: {
      alignSelf: "start",
      padding: 25,
      paddingTop: 10,
      width: "100%",
      [theme.breakpoints.down("md")]: {
        display: "flex",
        justifyContent: "space-around",
      },
      [theme.breakpoints.down("sm")]: {
        flexDirection: "column",
        alignItems: "center",
      },
      [theme.breakpoints.down("xs")]: {
        alignItems: "start",
      },
    },
  }));

  const classes = useStyles();

  if (!coin) return <LinearProgress style={{ background: "gold" }} />;

  return (
    <div className={classes.container}>
      <div className={classes.sidebar}>
        <img
          src={coin?.image.large}
          alt={coin?.name}
          height="200"
          style={{ marginBottom: 20 }}
        />
        <Typography variant="h3" className={classes.heading}>
          {coin?.name}
        </Typography>
        <Typography variant="subtitle1" className={classes.description}>
          {coin?.description.en.split(". ")[0]}
        </Typography>
        <div className={classes.marketData}>
          {/* Rank */}
          <span
            style={{
              display: "flex",
            }}
          >
            <Typography variant="h6" className={classes.heading}>
              Rank :
            </Typography>
            &nbsp; &nbsp;
            <Typography variant="h6" style={{ fontFamily: "Montserrat" }}>
              {coin?.market_cap_rank}
            </Typography>
          </span>

          {/* current price */}
          <span
            style={{
              display: "flex",
            }}
          >
            <Typography variant="h6" className={classes.heading}>
              Current Price :
            </Typography>
            &nbsp; &nbsp;
            <Typography variant="h6" style={{ fontFamily: "Montserrat" }}>
              {symbol}{" "}
              {numberWithCommas(
                coin?.market_data.current_price[currency.toLowerCase()]
              )}
            </Typography>
          </span>

          {/* Market Cap */}
          <span
            style={{
              display: "flex",
            }}
          >
            <Typography variant="h6" className={classes.heading}>
              Market Cap :
            </Typography>
            &nbsp; &nbsp;
            <Typography variant="h6" style={{ fontFamily: "Montserrat" }}>
              {symbol}{" "}
              {numberWithCommas(
                coin?.market_data.market_cap[currency.toLowerCase()]
                  .toString()
                  .slice(0, -6)
              )}{" "}
              M
            </Typography>
          </span>
        </div>
      </div>

      {/*chart*/}
      <CoinInfo coin={coin}></CoinInfo>
    </div>
  );
};

export default CoinPage;
