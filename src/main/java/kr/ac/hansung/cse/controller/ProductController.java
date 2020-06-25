package kr.ac.hansung.cse.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.ac.hansung.cse.model.Product;
import kr.ac.hansung.cse.repo.ProductRepository;

//@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1")
public class ProductController {

	@Autowired
	ProductRepository repository;

	@GetMapping("/products")
	public ResponseEntity<List<Product>> getAllProducts() {
		List<Product> products = new ArrayList<>();
		try {
			//모든 레코드를 조회
			repository.findAll().forEach(products::add);

			// 비어있으면 
			if (products.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			//있으면 리턴해줌
			return new ResponseEntity<>(products, HttpStatus.OK);
		} catch (Exception e) {
			//없으면 exception!
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/products/{id}")
	public ResponseEntity<Product> getCustomerById(@PathVariable("id") long id) {
		//있을 수도있고 없을 수도있어서 Optional!
		Optional<Product> productData = repository.findById(id);

		if (productData.isPresent()) {
			return new ResponseEntity<>(productData.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@PostMapping(value = "/products")
	public ResponseEntity<Product> postCustomer(@RequestBody Product product) {
		//사용자의 정보를 받아오면 정보를 받아서 repository에 세이브!
		try {
			Product _product = repository.save(new Product(product.getId(), product.getName(), product.getCategory(), 
												product.getPrice(),product.getManufacturer(), product.getUnitInStock(),
												product.getDescription()));
			return new ResponseEntity<>(_product, HttpStatus.CREATED);
		} catch (Exception e) {
			return new ResponseEntity<>(null, HttpStatus.EXPECTATION_FAILED);
		}
	}

	@DeleteMapping("/products/{id}")
	public ResponseEntity<HttpStatus> deleteCustomer(@PathVariable("id") long id) {
		try {
			repository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}


	@GetMapping(value = "products/category/{category}")
	//findById를 바탕으로 조회했지만 이번엔 category를 통해서 조회
	public ResponseEntity<List<Product>> findById(@PathVariable String category) {
		try {
			List<Product> products = repository.findByCategory(category);

			if (products.isEmpty()) {
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(products, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.EXPECTATION_FAILED);
		}
	}

	//조회하는거 고객이 정보를 넣어주면 그거를 수정함!
	@PutMapping("/products/{id}")
	public ResponseEntity<Product> updateCustomer(@PathVariable("id") long id, @RequestBody Product customer) {
		Optional<Product> customerData = repository.findById(id);

		if (customerData.isPresent()) {
			Product _customer = customerData.get();
			_customer.setName(customer.getName());
			_customer.setCategory(customer.getCategory());
			_customer.setPrice(customer.getPrice());
			_customer.setManufacturer(customer.getManufacturer());
			_customer.setUnitInStock(customer.getUnitInStock());
			_customer.setDescription(customer.getDescription());
			return new ResponseEntity<>(repository.save(_customer), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
