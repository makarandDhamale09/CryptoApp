import React, { useEffect, useState } from "react";
import { CoinList } from "../config/api";
import axios from "axios";
import { CryptoState } from "../CryptoContext";
import {
  Container,
  LinearProgress,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  ThemeProvider,
  Typography,
  createTheme,
  makeStyles,
} from "@material-ui/core";
import { useNavigate } from "react-router-dom";
import { Classnames } from "react-alice-carousel";

const CoinsTable = () => {
  const [coins, setCoins] = useState([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState("");

  const { currency } = CryptoState();

  const useStyles = makeStyles(() => ({}));

  const classes = useStyles();

  const fetchCoins = async () => {
    setLoading(true);
    const { data } = await axios.get(CoinList(currency));
    console.log(data);
    setCoins(data);
    setLoading(false);
  };

  const navigate = useNavigate();

  useEffect(() => {
    fetchCoins();
  }, [currency]);

  const darkTheme = createTheme({
    palette: {
      primary: {
        main: "#fff",
      },
      type: "dark",
    },
  });

  const handleSearch = () => {
    return coins.filter((coin) => {
      coin.name.toLowerCase().includes(search) ||
        coin.symbol.toLowerCase().includes(search);
    });
  };

  return (
    <ThemeProvider theme={darkTheme}>
      <Container style={{ textAlign: "center" }}>
        <Typography
          variant="h4"
          style={{ margin: 18, fontFamily: "Montserrat" }}
        >
          Cryptocurrency Price by Market Cap
        </Typography>
        <TextField
          label="Search for CryptoCurrency..."
          variant="outlined"
          style={{ marginBottom: 20, width: "100%" }}
          onChange={(e) => setSearch(e.target.value)}
        ></TextField>
        <TableContainer>
          {loading ? (
            <LinearProgress style={{ background: "gold" }} />
          ) : (
            <Table>
              <TableHead style={{ background: "#EEBC1D" }}>
                <TableRow>
                  {["Coin", "Price", "24h Change", "Market Cap"].map((head) => (
                    <TableCell
                      style={{
                        color: "black",
                        fontWeight: "700",
                        fontFamily: "Montserrat",
                      }}
                      key={head}
                      align={head === "Coin" ? "left" : "right"}
                    >
                      {head}
                    </TableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {handleSearch().map((row) => {
                  const profit = row.profit_change_percentage_24h > 0;
                  console.log("row :" + row);
                  return (
                    <TableRow
                      onClick={() => {
                        navigate(`/coins/${row.id}`);
                      }}
                      className={classes.row}
                      key={row.name}
                    >
                      <TableCell
                        component="th"
                        scope="row"
                        style={{ display: "flex", gap: 15 }}
                      >
                        <img
                          src={row?.image}
                          alt={row.name}
                          height="50"
                          style={{ marginBottom: 10 }}
                        />
                      </TableCell>
                    </TableRow>
                  );
                })}
              </TableBody>
            </Table>
          )}
        </TableContainer>
      </Container>
    </ThemeProvider>
  );
};

export default CoinsTable;
